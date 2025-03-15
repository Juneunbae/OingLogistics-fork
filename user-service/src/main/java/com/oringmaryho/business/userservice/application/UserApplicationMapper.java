package com.oringmaryho.business.userservice.application;

import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserApplicationMapper {

  UserSignInResponseDto toSignInResponseDto(UserSignInResponseServiceDto responseServiceDto);

  UserSearchResponseDto toSearchResponseDto(UserSearchResponseServiceDto responseServiceDto);
}
