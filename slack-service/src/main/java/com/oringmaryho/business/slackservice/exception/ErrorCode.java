package com.oringmaryho.business.slackservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("S-001", "해당 메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
