package com.oringmaryho.business.userservice.presentation;

import com.oringmaryho.business.userservice.application.dto.request.UserMasterCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterCreateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSearchRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignInRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;
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


  @Mapping(target = "", source = "")
  @Mapping(target = "", source = "")
  @Mapping(target = "", source = "")
  @Mapping(target = "", source = "")
  UserMasterSearchRequestServiceDto toUserMasterSearchRequestServiceDto(
      UserMasterSearchRequestDto requestDto);
}
