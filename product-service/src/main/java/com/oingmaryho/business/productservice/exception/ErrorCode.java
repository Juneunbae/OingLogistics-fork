package com.oingmaryho.business.productservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("P-001", "해당 업체를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_PRICE_RANGE("C-002", "최소 가격은 최대 가격보다 클 수 없습니다.", HttpStatus.BAD_REQUEST),
	INVALID_STOCK_RANGE("C-003", "최소 재고는 최대 재고보다 클 수 없습니다.", HttpStatus.BAD_REQUEST),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
