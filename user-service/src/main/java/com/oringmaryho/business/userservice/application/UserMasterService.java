package com.oringmaryho.business.userservice.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oringmaryho.business.userservice.application.dto.request.UserMasterCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserMasterUpdateRoleResponseServiceDto;
import com.oringmaryho.business.userservice.infrastructure.UserRepository;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterUpdateRoleResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMasterService {

	private UserRepository userRepository;
	private UserApplicationMapper userApplicationMapper;

	public void signUpUserMaster(UserMasterSignUpRequestServiceDto requestServiceDto) {

	}

	public void createUser(UserMasterCreateRequestServiceDto requestServiceDto) {

	}

	public void slackConfirmUser(UserSlackConfirmRequestServiceDto requestServiceDto) {

	}

	public void findUserMaster(UserMasterFindRequestServiceDto requestServiceDto) {

	}

	public List<UserMasterSearchResponseDto> searchUsers(
		UserMasterSearchRequestServiceDto requestServiceDto) {
		return null;
	}

	public UserMasterUpdateResponseDto updateUser(
		UserMasterUpdateRequestServiceDto requestServiceDto) {
		//todo: responseDto 반환
		Long userId = null;
		UserMasterUpdateResponseDto responseDto = userApplicationMapper.toUserMasterUpdateResponseDto(
			userId);
		return responseDto;
	}

	public UserMasterGrantRoleResponseDto grantRoleUser(
		UserMasterGrantRoleRequestServiceDto requestServiceDto) {
		Long userId = null;
		UserMasterGrantRoleResponseDto responseDto = userApplicationMapper.toUserMasterGrantRoleResponseDto(
			userId);
		return responseDto;
	}

	public UserMasterUpdateRoleResponseDto updateRoleUser(
		UserMasterUpdateRoleRequestServiceDto requestServiceDto) {
		//todo: 유저 변경 전 role 받아와서 묶어서 반환하기
		UserMasterUpdateRoleResponseServiceDto responseServiceDto = null;
		UserMasterUpdateRoleResponseDto responseDto = userApplicationMapper.toUserMasterUpdateRoleResponseDto(
			responseServiceDto);
		return null;
	}

	public void deleteRoleUser(UserMasterDeleteRoleRequestServiceDto requestServiceDto) {

	}

	public void deleteUser(UserMasterDeleteRequestServiceDto requestServiceDto) {

	}
}
