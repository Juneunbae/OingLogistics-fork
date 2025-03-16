package com.oringmaryho.business.userservice.application;

import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterUpdateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserApplicationMapper {

  UserSignInResponseDto toSignInResponseDto(UserSignInResponseServiceDto responseServiceDto);

  UserSearchResponseDto toSearchResponseDto(UserSearchResponseServiceDto responseServiceDto);

  @Mapping(target = "id", source = "id")
  UserMasterUpdateResponseDto toUserMasterUpdateResponseDto(Long id);

  @Mapping(target = "id", source = "id")
  UserMasterGrantRoleResponseDto toUserMasterGrantRoleResponseDto(Long id);
}
