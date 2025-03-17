package com.oringmaryho.business.userservice.presentation.dto.response;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchResponseDto(Long id, String userName, String slackId, UserRoleType role, Boolean isDeleted) {

}
