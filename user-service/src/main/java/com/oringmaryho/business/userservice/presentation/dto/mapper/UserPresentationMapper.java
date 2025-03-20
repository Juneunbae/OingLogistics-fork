package com.oringmaryho.business.userservice.presentation.dto.mapper;

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
import com.oringmaryho.business.userservice.application.dto.request.UserSlackCodeRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminCreateRequestDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminUpdateRoleRequestDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminGrantRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSearchRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminUpdateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignInRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackCodeRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {

	UserSignUpRequestServiceDto toUserSignUpServiceDto(UserSignUpRequestDto requestDto);

	UserSignInRequestServiceDto toUserSignInServiceDto(UserSignInRequestDto requestDto);

	@Mapping(target = "id", source = "id")
	UserSearchRequestServiceDto toUserSearchRequestServiceDto(Long id);

	UserSlackConfirmRequestServiceDto toUserSlackConfirmRequestServiceDto(
		UserSlackConfirmRequestDto requestDto);

	UserAdminSignUpRequestServiceDto toUserAdminSignUpServiceDto(
		UserAdminSignUpRequestDto requestDto);

	UserAdminCreateRequestServiceDto toUserAdminCreateRequestServiceDto(
		UserAdminCreateRequestDto requestDto);

	@Mapping(target = "id", source = "id")
	UserAdminFindRequestServiceDto toUserAdminFindRequestServiceDto(Long id);


	UserAdminSearchRequestServiceDto toUserAdminSearchRequestServiceDto(
		UserAdminSearchRequestDto requestDto, Pageable pageable);

	@Mapping(target = "id", expression = "java(id)")
	UserAdminUpdateRequestServiceDto toUserAdminUpdateRequestServiceDto(UserAdminUpdateRequestDto requestDto,
		Long id);

	@Mapping(target = "id", expression = "java(id)")
	UserAdminGrantRoleRequestServiceDto toUserAdminGrantRoleRequestServiceDto(UserAdminGrantRoleRequestDto requstDto,
		Long id);

	@Mapping(target = "id", expression = "java(id)")
	@Mapping(target = "newRole", expression = "java(requestDto.role())")
	UserAdminUpdateRoleRequestServiceDto toUserAdminUpdateRoleRequestServiceDto(
		UserAdminUpdateRoleRequestDto requestDto, Long id);

	@Mapping(target = "id", source = "id")
	UserAdminDeleteRoleRequestServiceDto toUserAdminDeleteRoleRequestServiceDto(Long id);

	@Mapping(target = "id", source = "id")
	UserAdminDeleteRequestServiceDto toUserAdminDeleteRequestServiceDto(Long id);

	UserSlackCodeRequestServiceDto toUserSlackCodeRequestServiceDto(UserSlackCodeRequestDto requestDto);
}
