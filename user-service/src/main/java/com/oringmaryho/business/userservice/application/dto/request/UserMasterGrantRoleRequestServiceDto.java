package com.oringmaryho.business.userservice.application.dto.request;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserMasterGrantRoleRequestServiceDto(Long id, UserRoleType role) {

}
