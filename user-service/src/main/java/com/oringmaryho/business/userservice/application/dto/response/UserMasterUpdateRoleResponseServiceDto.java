package com.oringmaryho.business.userservice.application.dto.response;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserMasterUpdateRoleResponseServiceDto(Long id, UserRoleType role, UserRoleType newRole) {

}
