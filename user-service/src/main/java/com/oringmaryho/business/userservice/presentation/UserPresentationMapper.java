package com.oringmaryho.business.userservice.presentation;

import com.oringmaryho.business.userservice.application.dto.request.UserMasterCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterCreateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterGrantRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSearchRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterUpdateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterUpdateRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignInRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;
import org.springframework.data.domain.Pageable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {

  UserSignUpRequestServiceDto toUserSignUpServiceDto(UserSignUpRequestDto requestDto);

  UserSignInRequestServiceDto toUserSignInServiceDto(UserSignInRequestDto requestDto);

  @Mapping(target = "id", source = "id")
  UserSearchRequestServiceDto toUserSearchRequestServiceDto(Long id);

  UserSlackConfirmRequestServiceDto toUserSlackConfirmRequestServiceDto(
      UserSlackConfirmRequestDto requestDto);

  UserMasterSignUpRequestServiceDto toUserMasterSignUpServiceDto(
      UserMasterSignUpRequestDto requestDto);

  UserMasterCreateRequestServiceDto toUserMasterCreateRequestServiceDto(
      UserMasterCreateRequestDto requestDto);

  @Mapping(target = "id", source = "id")
  UserMasterFindRequestServiceDto toUserMasterFindRequestServiceDto(Long id);


  UserMasterSearchRequestServiceDto toUserMasterSearchRequestServiceDto(
      UserMasterSearchRequestDto requestDto, Pageable pageable);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "password", source = "password")
  @Mapping(target = "slackId", source = "slackId")
  UserMasterUpdateRequestServiceDto toUserMasterUpdateRequestServiceDto(Long id, UserMasterUpdateRequestDto requstDto);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "role", source = "role")
  UserMasterGrantRoleRequestServiceDto toUserMasterGrantRoleRequestServiceDto(Long id, UserMasterGrantRoleRequestDto requstDto);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "newRole", source = "role")
  UserMasterUpdateRoleRequestServiceDto toUserMasterUpdateRoleRequestServiceDto(Long id, UserMasterUpdateRoleRequestDto requstDto);

  @Mapping(target = "id", source = "id")
  UserMasterDeleteRoleRequestServiceDto toUserMasterDeleteRoleRequestServiceDto(Long id);
}
