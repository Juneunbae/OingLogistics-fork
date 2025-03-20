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
	USER_NOT_MATCH("U-006", "요청한 사용자와 정보가 다릅니다.", HttpStatus.BAD_REQUEST),

	SLACK_API_ERROR("U-007", "Slack API 호출 실패.", HttpStatus.BAD_REQUEST),
	SLACK_INVALID_RESPONSE("U-008", "SLACK 응답 형식이 다릅니다.", HttpStatus.BAD_REQUEST),
	SLACK_AUTH_FAIL("U-009", "SLACK 계정 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
	SLACK_ALREADY_AUTH("U-010", "이미 인증된 SLACK 계정입니다.", HttpStatus.BAD_REQUEST),

	STORAGE_NEGATIVE_ERROR("U-011", "TTL이 음수일 수 없습니다.", HttpStatus.BAD_REQUEST),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
