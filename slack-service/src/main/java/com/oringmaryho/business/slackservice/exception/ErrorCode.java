package com.oringmaryho.business.slackservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("S-001", "해당 메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	SLACK_API_ERROR("S-002", "Slack API 호출 실패.", HttpStatus.BAD_REQUEST),
	SLACK_INVALID_RESPONSE("S-003", "SLACK 응답 형식이 다릅니다.", HttpStatus.BAD_REQUEST),
	SLACK_AUTH_FAIL("S-004", "SLACK 계정 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
	SLACK_ALREADY_AUTH("S-005", "이미 인증된 SLACK 계정입니다.", HttpStatus.BAD_REQUEST),
	SLACK_ID_EMPTY("S-006", "SLACK ID가 없습니다.", HttpStatus.BAD_REQUEST)
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
