package com.oringmaryho.business.userservice.presentation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

import com.oringmaryho.business.userservice.application.dto.request.UserAdminCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminCreateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminUpdateRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminGrantRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSearchRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminUpdateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignInRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {

	UserSignUpRequestServiceDto toUserSignUpServiceDto(UserSignUpRequestDto requestDto);

	UserSignInRequestServiceDto toUserSignInServiceDto(UserSignInRequestDto requestDto);

	@Mapping(target = "id", source = "id")
	UserSearchRequestServiceDto toUserSearchRequestServiceDto(Long id);

	UserSlackConfirmRequestServiceDto toUserSlackConfirmRequestServiceDto(
		UserSlackConfirmRequestDto requestDto);

	UserAdminSignUpRequestServiceDto toUserMasterSignUpServiceDto(
		UserAdminSignUpRequestDto requestDto);

	UserAdminCreateRequestServiceDto toUserMasterCreateRequestServiceDto(
		UserAdminCreateRequestDto requestDto);

	@Mapping(target = "id", source = "id")
	UserAdminFindRequestServiceDto toUserMasterFindRequestServiceDto(Long id);

	UserAdminSearchRequestServiceDto toUserMasterSearchRequestServiceDto(
		UserAdminSearchRequestDto requestDto, Pageable pageable);

	@Mapping(target = "id", expression = "java(id)")
	UserAdminUpdateRequestServiceDto toUserMasterUpdateRequestServiceDto(UserAdminUpdateRequestDto requestDto,
		Long id);

	@Mapping(target = "id", expression = "java(id)")
	UserAdminGrantRoleRequestServiceDto toUserMasterGrantRoleRequestServiceDto(UserAdminGrantRoleRequestDto requstDto,
		Long id);

	@Mapping(target = "id", expression = "java(id)")
	@Mapping(target = "newRole", expression = "java(requestDto.role())")
	UserAdminUpdateRoleRequestServiceDto toUserMasterUpdateRoleRequestServiceDto(
		UserAdminUpdateRoleRequestDto requestDto, Long id);

	@Mapping(target = "id", source = "id")
	UserMasterDeleteRoleRequestServiceDto toUserMasterDeleteRoleRequestServiceDto(Long id);

	@Mapping(target = "id", source = "id")
	UserAdminDeleteRequestServiceDto toUserMasterDeleteRequestServiceDto(Long id);
}
