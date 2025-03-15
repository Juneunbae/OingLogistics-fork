package com.oringmaryho.business.userservice.presentation;

import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignInRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {

  UserPresentationMapper INSTANCE = Mappers.getMapper(UserPresentationMapper.class);

  UserSignUpRequestServiceDto toUserSignUpServiceDto(UserSignUpRequestDto requestDto);

  UserSignInRequestServiceDto toUserSignInServiceDto(UserSignInRequestDto requestDto);

  UserSearchRequestServiceDto toUserSearchRequestServiceDto(Long id);

  UserSlackConfirmRequestServiceDto toUserSlackConfirmRequestServiceDto(
      UserSlackConfirmRequestDto requestDto);
}
