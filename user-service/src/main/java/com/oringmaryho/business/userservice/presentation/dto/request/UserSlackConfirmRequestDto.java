package com.oringmaryho.business.userservice.presentation.dto.request;

public record UserSlackConfirmRequestDto(String username, String slackId, String confirmCode) {

}
