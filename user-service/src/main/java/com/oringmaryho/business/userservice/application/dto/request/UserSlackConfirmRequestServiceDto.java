package com.oringmaryho.business.userservice.application.dto.request;

public record UserSlackConfirmRequestServiceDto(String username, String slackId, String confirmCode) {

}
