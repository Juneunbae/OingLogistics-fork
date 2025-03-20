package com.oingmaryho.business.productservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("P-001", "해당 업체를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
