package com.oringmaryho.business.userservice.presentation.dto.response;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminUpdateRoleResponseDto(Long id, UserRoleType role, UserRoleType newRole) {

}
