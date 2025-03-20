package com.oringmaryho.business.userservice.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.oringmaryho.business.userservice.application.dto.response.UserAdminFindResponseDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;

@Mapper(componentModel = "spring")
public interface UserApplicationMapper {

	UserSignInResponseDto toSignInResponseDto(UserSignInResponseServiceDto responseServiceDto);

	UserSearchResponseDto toSearchResponseDto(UserSearchResponseServiceDto responseServiceDto);

	@Mapping(target = "id", source = "id")
	UserAdminUpdateResponseDto toUserAdminUpdateResponseDto(Long id);

	@Mapping(target = "id", source = "id")
	UserAdminGrantRoleResponseDto toUserAdminGrantRoleResponseDto(Long id);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "role", source = "role")
	@Mapping(target = "newRole", source = "newRole")
	UserAdminUpdateRoleResponseDto toUserAdminUpdateRoleResponseDto(
		Long id, UserRoleType role, UserRoleType newRole);

	UserAdminFindResponseDto toUserAdminFindResponseDto(User user);

	UserAdminSearchResponseDto toUserAdminSearchResponseDto(User user);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "username", source = "username")
	@Mapping(target = "password", source = "password")
	@Mapping(target = "slackId", source = "slackId")
	@Mapping(target = "role", source = "role")
	@Mapping(target = "status", source = "status")
	UserSearchResponseDto toUserSearchResponseDto(User user);
}
