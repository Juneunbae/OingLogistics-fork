package com.oringmaryho.business.userservice.presentation.dto.request;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminCreateRequestDto(String username, String password, String slackId, UserRoleType role) {

}
