package com.oringmaryho.business.userservice.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oringmaryho.business.userservice.application.dto.response.UserMasterUpdateRoleResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterUpdateRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;

@Mapper(componentModel = "spring")
public interface UserApplicationMapper {

	UserSignInResponseDto toSignInResponseDto(UserSignInResponseServiceDto responseServiceDto);

	UserSearchResponseDto toSearchResponseDto(UserSearchResponseServiceDto responseServiceDto);

	@Mapping(target = "id", source = "id")
	UserMasterUpdateResponseDto toUserMasterUpdateResponseDto(Long id);

	@Mapping(target = "id", source = "id")
	UserMasterGrantRoleResponseDto toUserMasterGrantRoleResponseDto(Long id);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "role", source = "role")
	@Mapping(target = "newRole", source = "newRole")
	UserMasterUpdateRoleResponseDto toUserMasterUpdateRoleResponseDto(
		UserMasterUpdateRoleResponseServiceDto responseServiceDto);
}
