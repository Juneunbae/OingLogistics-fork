package com.oringmaryho.business.userservice.application.dto.response;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserSearchResponseServiceDto(Long id, String userName, String slackId,
                                           UserRoleType role) {

}
