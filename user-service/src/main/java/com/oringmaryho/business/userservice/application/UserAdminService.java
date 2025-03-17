package com.oringmaryho.business.userservice.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oringmaryho.business.userservice.application.dto.request.UserAdminCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserAdminUpdateRoleResponseServiceDto;
import com.oringmaryho.business.userservice.infrastructure.UserRepository;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAdminService {

	private UserRepository userRepository;
	private UserApplicationMapper userApplicationMapper;

	public void signUpUserMaster(UserAdminSignUpRequestServiceDto requestServiceDto) {

	}

	public void createUser(UserAdminCreateRequestServiceDto requestServiceDto) {

	}

	public void slackConfirmUser(UserSlackConfirmRequestServiceDto requestServiceDto) {

	}

	public void findUserMaster(UserAdminFindRequestServiceDto requestServiceDto) {

	}

	public List<UserAdminSearchResponseDto> searchUsers(
		UserAdminSearchRequestServiceDto requestServiceDto) {
		return null;
	}

	public UserAdminUpdateResponseDto updateUser(
		UserAdminUpdateRequestServiceDto requestServiceDto) {
		//todo: responseDto 반환
		Long userId = null;
		UserAdminUpdateResponseDto responseDto = userApplicationMapper.toUserMasterUpdateResponseDto(
			userId);
		return responseDto;
	}

	public UserAdminGrantRoleResponseDto grantRoleUser(
		UserAdminGrantRoleRequestServiceDto requestServiceDto) {
		Long userId = null;
		UserAdminGrantRoleResponseDto responseDto = userApplicationMapper.toUserMasterGrantRoleResponseDto(
			userId);
		return responseDto;
	}

	public UserAdminUpdateRoleResponseDto updateRoleUser(
		UserAdminUpdateRoleRequestServiceDto requestServiceDto) {
		//todo: 유저 변경 전 role 받아와서 묶어서 반환하기
		UserAdminUpdateRoleResponseServiceDto responseServiceDto = null;
		UserAdminUpdateRoleResponseDto responseDto = userApplicationMapper.toUserMasterUpdateRoleResponseDto(
			responseServiceDto);
		return null;
	}

	public void deleteRoleUser(UserMasterDeleteRoleRequestServiceDto requestServiceDto) {

	}

	public void deleteUser(UserAdminDeleteRequestServiceDto requestServiceDto) {

	}
}
