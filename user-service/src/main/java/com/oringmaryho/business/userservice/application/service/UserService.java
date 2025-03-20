package com.oringmaryho.business.userservice.application.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oringmaryho.business.userservice.application.mapper.UserApplicationMapper;
import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackCodeRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.application.utils.CodeStorage;
import com.oringmaryho.business.userservice.application.utils.DirectMessageAuthService;
import com.oringmaryho.business.userservice.application.utils.RedisUtil;
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

	private final RedisUtil redisUtil;
	private final RedisTemplate<String, Object> redisTemplate;

	private final CodeStorage codeStorage;

	@Value("${slack.code.ttl}")
	private Long SLACK_CODE_TTL;

	private static final String USERNAME_REGEX = "^[a-z0-9]{4,10}$";
	private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,15}$";

	@Transactional
	public void signUpUser(UserSignUpRequestServiceDto requestServiceDto) {

		//null처리
		validateRequiredField(requestServiceDto.username(), ErrorCode.USERNAME_NULL);
		validateRequiredField(requestServiceDto.password(), ErrorCode.PASSWORD_NULL);
		validateRequiredField(requestServiceDto.slackId(), ErrorCode.SLACKID_NULL);

		//형식에 맞는지 체크
		usernameVerify(requestServiceDto.username());
		passwordVerify(requestServiceDto.password());

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

		redisUtil.updateUserInfo(user);
		Map<String, String> tokenMap = redisUtil.updateUserJwtToken(user);

		// 응답 DTO 생성
		UserSignInResponseServiceDto serviceDto = new UserSignInResponseServiceDto(
			tokenMap.get("accessToken"),
			tokenMap.get("refreshToken")
		);

		return userApplicationMapper.toSignInResponseDto(serviceDto);
	}

	public UserSearchResponseDto searchUser(UserSearchRequestServiceDto requestServiceDto) {
		UserSearchResponseServiceDto userSearchResponseServiceDto = null;
		return userApplicationMapper.toSearchResponseDto(userSearchResponseServiceDto);
	}

	public void slackCodeRequestUser(UserSlackCodeRequestServiceDto requestServiceDto) {
		validateRequiredField(requestServiceDto.username(), ErrorCode.USERNAME_NULL);
		validateRequiredField(requestServiceDto.slackId(), ErrorCode.SLACKID_NULL);

		if (!userRepository.existsByUsername(requestServiceDto.username())) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}

		User user = userRepository.findByUsername(requestServiceDto.username())
			.orElseThrow(()-> new UserException(ErrorCode.NOT_FOUND));

		if(!requestServiceDto.slackId().equals(user.getSlackId())
		|| !requestServiceDto.username().equals(user.getUsername())) {
			throw new UserException(ErrorCode.USER_NOT_MATCH);
		}

		if(user.getStatus().equals(UserConfirmStatus.CONFIRMED)){
			throw new UserException(ErrorCode.SLACK_ALREADY_AUTH);
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

	@Transactional
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
			user.changeStatus(UserConfirmStatus.CONFIRMED);

			codeStorage.removeCode(requestServiceDto.username());
		} else {
			throw new UserException(ErrorCode.SLACK_AUTH_FAIL);
		}

		User updatedUser = userRepository.findByUsername(requestServiceDto.username())
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

		redisUtil.updateUserInfo(updatedUser);
	}

	//username 형식에 맞는지 체크
	public void usernameVerify(String username) {
		if (!Pattern.matches(USERNAME_REGEX, username)) {
			throw new UserException(ErrorCode.USERNAME_REGEX_NOT_MATCH);
		}
	}

	//password 형식에 맞는지 체크
	public void passwordVerify(String password) {
		if (!Pattern.matches(PASSWORD_REGEX, password)) {
			throw new UserException(ErrorCode.PASSWORD_REGEX_NOT_MATCH);
		}
	}

	//null 체크
	private void validateRequiredField(String value, ErrorCode errorCode) {
		if (value == null || value.isEmpty()) {
			throw new UserException(errorCode);
		}
	}

}
