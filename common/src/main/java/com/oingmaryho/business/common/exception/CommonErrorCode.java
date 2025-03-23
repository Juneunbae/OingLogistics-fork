package com.oingmaryho.business.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {

	FORBIDDEN("B-001", "권한이 없습니다.", HttpStatus.FORBIDDEN),
	UNAUTHORIZED("B-002", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
	USER_NOT_FOUND("B-003", "사용자 정보를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
	INVALID_USER_STATUS("B-004", "확인되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
	INVALID_ROLE("B-005", "해당 작업을 수행할 수 없는 역할입니다.", HttpStatus.FORBIDDEN),
	MISSING_USER_INFO("B-006", "사용자 정보가 유실되었습니다.", HttpStatus.UNAUTHORIZED),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
