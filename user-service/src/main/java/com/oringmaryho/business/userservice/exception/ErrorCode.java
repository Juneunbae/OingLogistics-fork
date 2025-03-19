package com.oringmaryho.business.userservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("U-001", "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	USERNAME_NULL("U-002", "Username이 비어 있습니다.", HttpStatus.BAD_REQUEST),
	PASSWORD_NULL("U-003", "Password가 비어 있습니다.", HttpStatus.BAD_REQUEST),
	SLACKID_NULL("U-004", "Slack Id가 비어 있습니다.", HttpStatus.BAD_REQUEST),
	ALREADY_EXISTS("U-005", "이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST),

	SLACK_API_ERROR("U-006", "Slack API 호출 실패.", HttpStatus.BAD_REQUEST),
	SLACK_INVALID_RESPONSE("U-007", "SLACK 응답 형식이 다릅니다.", HttpStatus.BAD_REQUEST),
	SLACK_AUTH_FAIL("U-008", "SLACK 계정 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
