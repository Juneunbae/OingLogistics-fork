package com.oingmaryho.business.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {

	FORBIDDEN("B-001", "권한이 없습니다.", HttpStatus.FORBIDDEN);

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
