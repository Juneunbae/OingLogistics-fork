package com.oringmaryho.business.userservice.application.dto.request;

public record UserAdminSlackConfirmRequestServiceDto(String username, String slackId, String confirmCode) {
}
