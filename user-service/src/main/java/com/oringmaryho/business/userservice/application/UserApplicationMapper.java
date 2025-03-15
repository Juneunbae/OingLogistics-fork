package com.oringmaryho.business.userservice.application;

import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;
import org.mapstruct.factory.Mappers;

public interface UserApplicationMapper {

  UserApplicationMapper INSTANCE = Mappers.getMapper(UserApplicationMapper.class);

  UserSignInResponseDto toSignInResponseDto(UserSignInResponseServiceDto responseServiceDto);

  UserSearchResponseDto toSearchResponseDto(UserSearchResponseServiceDto responseServiceDto);
}
