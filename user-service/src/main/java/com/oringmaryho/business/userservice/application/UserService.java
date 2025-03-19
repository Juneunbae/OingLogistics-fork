package com.oringmaryho.business.userservice.application;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackCodeRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.application.utils.CodeStorage;
import com.oringmaryho.business.userservice.application.utils.DirectMessageAuthService;
import com.oringmaryho.business.userservice.config.security.jwt.JwtTokenProvider;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final DirectMessageAuthService directMessageAuthService;

	private final RedisTemplate<String, Object> redisTemplate;

	private final CodeStorage codeStorage;

	@Value("${slack.code.ttl}")
	private Long SLACK_CODE_TTL;

	@Transactional
	public void signUpUser(UserSignUpRequestServiceDto requestServiceDto) {

		//null처리
		if (requestServiceDto.username() == null || requestServiceDto.username().isEmpty()) {
			throw new UserException(ErrorCode.USERNAME_NULL);
		}
		if (requestServiceDto.password() == null || requestServiceDto.password().isEmpty()) {
			throw new UserException(ErrorCode.PASSWORD_NULL);
		}
		if (requestServiceDto.slackId() == null || requestServiceDto.slackId().isEmpty()) {
			throw new UserException(ErrorCode.SLACKID_NULL);
		}

		// username 중복 체크
		if (userRepository.existsByUsername(requestServiceDto.username())) {
			throw new UserException(ErrorCode.ALREADY_EXISTS);
		}

		// 비번 암호화
		String encodedPassword = passwordEncoder.encode(requestServiceDto.password());

		// DTO -> Entity 변환 후 저장
		User user = User.builder()
			.username(requestServiceDto.username())
			.password(encodedPassword)
			.slackId(requestServiceDto.slackId())
			.build();

		userRepository.save(user);
	}

	public UserSignInResponseDto signInUser(UserSignInRequestServiceDto requestServiceDto) {
		// 사용자 인증
		if (!userRepository.existsByUsername(requestServiceDto.username())) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}

		// 인증된 사용자 정보 가져오기
		User user = userRepository.findByUsername(requestServiceDto.username()).orElseThrow(
			() -> new EntityNotFoundException("User not found with username: " + requestServiceDto.username())
		);

		Map<String, Object> userInfoMap = new ConcurrentHashMap<>();
		userInfoMap.put("username", user.getUsername());
		userInfoMap.put("slackId", user.getSlackId());
		userInfoMap.put("role", user.getRole());
		userInfoMap.put("status", user.getStatus());

		// JWT 토큰 생성
		String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

		// Redis에 사용자 정보 + 토큰 저장
		// 유저 정보 키
		String userInfoKey = "user:info:" + user.getId();
		// 토큰 정보 키
		String tokenKey = "user:token:" + user.getId();

		// 유저 정보 저장
		redisTemplate.opsForValue().set(userInfoKey, userInfoMap);

		// 토큰 정보 저장
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("accessToken", accessToken);
		tokenMap.put("refreshToken", refreshToken);
		redisTemplate.opsForHash().putAll(tokenKey, tokenMap);

		// 만료 시간 설정 (둘 다 동일한 만료 시간 사용)
		long expirationTime = jwtTokenProvider.getRefreshTokenExpiration();
		redisTemplate.expire(userInfoKey, expirationTime, TimeUnit.MILLISECONDS);
		redisTemplate.expire(tokenKey, expirationTime, TimeUnit.MILLISECONDS);

		// 응답 DTO 생성
		UserSignInResponseServiceDto serviceDto = new UserSignInResponseServiceDto(
			accessToken,
			refreshToken
		);

		return userApplicationMapper.toSignInResponseDto(serviceDto);
	}

	public UserSearchResponseDto searchUser(UserSearchRequestServiceDto requestServiceDto) {
		UserSearchResponseServiceDto userSearchResponseServiceDto = null;
		return userApplicationMapper.toSearchResponseDto(userSearchResponseServiceDto);
	}

	public void slackCodeRequestUser(UserSlackCodeRequestServiceDto requestServiceDto) {
		if (requestServiceDto.username() == null || requestServiceDto.username().isEmpty()) {
			throw new UserException(ErrorCode.USERNAME_NULL);
		}
		if (requestServiceDto.slackId() == null || requestServiceDto.slackId().isEmpty()) {
			throw new UserException(ErrorCode.SLACKID_NULL);
		}
		if (!userRepository.existsByUsername(requestServiceDto.username())) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}

		//슬랙 코드 생성 및 codestorage에 저장
		String slackCode = directMessageAuthService.generateCode();

		directMessageAuthService.sendDirectMessage(requestServiceDto.slackId(), slackCode);

		//이전에 요청한 적 있는 유저 id라면 스토리지에 있는 내용 삭제 후 다시 저장
		if (codeStorage.hasKey(requestServiceDto.username())) {
			codeStorage.removeCode(requestServiceDto.username());
		}
		codeStorage.storeCode(requestServiceDto.username(), requestServiceDto.slackId(), slackCode,
			SLACK_CODE_TTL);

		//todo: slack 코드 생성하고 ttl 만큼 살려두고 삭제하는 테스트 작성하기
	}

	public void slackConfirmUser(UserSlackConfirmRequestServiceDto requestServiceDto) {
		String username = requestServiceDto.username();
		String slackId = requestServiceDto.slackId();

		String slackCode = codeStorage.getCode(requestServiceDto.username());

		if (slackCode != null
			&& codeStorage.getSlackUsername(username).equals(slackId)
			&& slackCode.equals(requestServiceDto.confirmCode())
		) {
			User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));
			User verifiedUser = User.builder()
				.id(user.getId())
				.username(user.getUsername())
				.password(user.getPassword())
				.slackId(user.getSlackId())
				.role(user.getRole())
				.status(UserConfirmStatus.CONFIRMED)
				.build();
			userRepository.save(verifiedUser);
			codeStorage.removeCode(requestServiceDto.username());
		} else {
			throw new UserException(ErrorCode.SLACK_AUTH_FAIL);
		}

	}

}
