package com.oingmaryho.business.hubservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("H-001", "해당 허브를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
