package com.oringmaryho.business.userservice.application.dto.response;

public record UserAdminSlackConfirmRequestDto(String username, String slcakId, String confirmCode) {
}
