package com.oringmaryho.business.userservice.application.dto.request;

import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchRequestServiceDto(
	Long id,
	String username,
	String slackId,
	UserRoleType role,
	UserConfirmStatus status,
	Boolean isDeleted
) {

}
