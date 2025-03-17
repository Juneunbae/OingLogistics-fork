package com.oringmaryho.business.userservice.application.dto.response;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchResponseServiceDto(Long id, String userName, String slackId,
												UserRoleType role, Boolean isDeleted) {

}
