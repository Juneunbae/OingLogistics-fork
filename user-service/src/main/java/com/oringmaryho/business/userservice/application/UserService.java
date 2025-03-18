package com.oringmaryho.business.userservice.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
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

		UserSignInResponseServiceDto userSignInResponseServiceDto = null;
		return userApplicationMapper.toSignInResponseDto(userSignInResponseServiceDto);
	}

	public UserSearchResponseDto searchUser(UserSearchRequestServiceDto requestServiceDto) {
		UserSearchResponseServiceDto userSearchResponseServiceDto = null;
		return userApplicationMapper.toSearchResponseDto(userSearchResponseServiceDto);
	}

	public void slackConfirmUser(UserSlackConfirmRequestServiceDto requestServiceDto) {

	}
}
