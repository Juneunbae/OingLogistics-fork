package com.oingmaryho.infrastructure.gateway_service.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	JWT_NOT_FOUND("GW-001", "JWT 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED),
	JWT_BLACKLISTED("GW-002", "JWT가 블랙리스트에 존재합니다.", HttpStatus.UNAUTHORIZED),
	JWT_EXPIRED("GW-003", "JWT가 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	WRONG_JWT("GW-004", "잘못된 JWT입니다.", HttpStatus.UNAUTHORIZED),
	JWT_VERIFIED_FAIL("GW-005", "JWT 검증 실패", HttpStatus.UNAUTHORIZED),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
