package com.oringmaryho.business.userservice.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oringmaryho.business.userservice.application.dto.response.UserAdminUpdateRoleResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;

@Mapper(componentModel = "spring")
public interface UserApplicationMapper {

	UserSignInResponseDto toSignInResponseDto(UserSignInResponseServiceDto responseServiceDto);

	UserSearchResponseDto toSearchResponseDto(UserSearchResponseServiceDto responseServiceDto);

	@Mapping(target = "id", source = "id")
	UserAdminUpdateResponseDto toUserMasterUpdateResponseDto(Long id);

	@Mapping(target = "id", source = "id")
	UserAdminGrantRoleResponseDto toUserMasterGrantRoleResponseDto(Long id);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "role", source = "role")
	@Mapping(target = "newRole", source = "newRole")
	UserAdminUpdateRoleResponseDto toUserMasterUpdateRoleResponseDto(
		UserAdminUpdateRoleResponseServiceDto responseServiceDto);
}
