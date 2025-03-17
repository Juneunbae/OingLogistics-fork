package com.oringmaryho.business.userservice.application;

import org.springframework.stereotype.Service;

import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.infrastructure.UserRepository;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private UserRepository userRepository;
	private UserApplicationMapper userApplicationMapper;

	public void signUpUser(UserSignUpRequestServiceDto requestServiceDto) {

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
