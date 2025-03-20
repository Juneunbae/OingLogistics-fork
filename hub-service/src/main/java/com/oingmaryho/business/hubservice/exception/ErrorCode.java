package com.oingmaryho.business.hubservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("H-001", "해당 허브를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	NOT_VALID_TIME("H-002", "시간은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
	NOT_VALID_DISTANCE("H-003", "거리는 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
