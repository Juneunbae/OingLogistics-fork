package com.oringmaryho.business.userservice.presentation.dto.response;

import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchResponseDto(Long id, String username, String slackId, UserRoleType role, UserConfirmStatus status, Boolean isDeleted) {

}
