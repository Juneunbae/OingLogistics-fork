package com.oringmaryho.business.userservice.presentation.dto.response;

import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserSearchResponseDto(Long id, String username, String password, String slackId, UserRoleType role, UserConfirmStatus status) {

}
