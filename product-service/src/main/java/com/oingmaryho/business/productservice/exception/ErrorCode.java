package com.oingmaryho.business.productservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("P-001", "해당 업체를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_PRICE_RANGE("P-002", "최소 가격은 최대 가격보다 클 수 없습니다.", HttpStatus.BAD_REQUEST),
	INVALID_STOCK_RANGE("P-003", "최소 재고는 최대 재고보다 클 수 없습니다.", HttpStatus.BAD_REQUEST),

	INVALID_COMPANY_NAME("P-004", "업체명은 공백이 될 수 없습니다.", HttpStatus.BAD_REQUEST),
	INVALID_PRODUCT_NAME("P-005", "상품명은 공백이 될 수 없습니다.", HttpStatus.BAD_REQUEST),
	INVALID_PRICE("P-006", "가격은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
	INVALID_STOCK("P-007", "재고는 0보다 작을 수 없습니다.", HttpStatus.BAD_REQUEST),
	OUT_OF_STOCK("P-008", "재고가 부족합니다.", HttpStatus.BAD_REQUEST ),

	HUB_NOT_FOUND("P-009", "허브 담당자 ID가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	COMPANY_NOT_FOUND("P-010", "업체 담당자 ID가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	NO_PERMISSION("P-011", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
	ALREADY_REGISTERED_PRODUCT("P-012","이미 등록된 상품입니다.", HttpStatus.CONFLICT),
	INVALID_COMPANY_TYPE("P-013","공급 업체만 상품을 등록하거나 수정할 수 있습니다.", HttpStatus.BAD_REQUEST),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
