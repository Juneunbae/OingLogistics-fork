package com.oringmaryho.business.userservice.application;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.config.security.jwt.JwtTokenProvider;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.infrastructure.UserRepository;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, Object> redisTemplate;
	private final AuthenticationManager authenticationManager;

	@Transactional
	public void signUpUser(UserSignUpRequestServiceDto requestServiceDto) {

		//null처리
		if (requestServiceDto.username() == null || requestServiceDto.username().isEmpty()) {
			throw new IllegalArgumentException("사용자 이름은 비어 있을 수 없습니다.");
		}
		if (requestServiceDto.password() == null || requestServiceDto.password().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
		}
		if (requestServiceDto.slackId() == null || requestServiceDto.slackId().isEmpty()) {
			throw new IllegalArgumentException("slackId는 비어 있을 수 없습니다.");
		}

		// username 중복 체크
		if (userRepository.existsByUsername(requestServiceDto.username())) {
			throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
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
		// 1. 사용자 인증(여기서 정보를 가져옴)
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				requestServiceDto.username(),
				requestServiceDto.password()
			)
		);

		// 2. 인증된 사용자 정보 가져오기(레디스에 저장하기 위해)
		User user = (User) authentication.getPrincipal();

		// 3. JWT 토큰 생성
		String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

		// 4. Redis에 사용자 정보 저장 (refresh token과 함께)
		String redisKey = "user:" + user.getId();
		redisTemplate.opsForValue().set(
			redisKey,
			user,
			jwtTokenProvider.getRefreshTokenExpiration(),
			TimeUnit.MILLISECONDS
		);

		// 5. 응답 DTO 생성
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

	public void slackConfirmUser(UserSlackConfirmRequestServiceDto requestServiceDto) {

	}
}
