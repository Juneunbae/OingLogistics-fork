package com.oringmaryho.business.userservice.application.dto.request;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminCreateRequestServiceDto(String username, String password, String slackId, UserRoleType role) {

}
