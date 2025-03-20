package com.oringmaryho.business.userservice.presentation.dto.request;

import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchRequestDto(
	Long id,
	String username,
	String slackId,
	UserRoleType role,
	UserConfirmStatus status,
	Boolean isDeleted
) {

}
