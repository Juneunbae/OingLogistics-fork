package com.oingmaryho.business.orderservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND("O-001", "주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_DETAIL_NOT_FOUND("0-002", "상세 주문 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMPANY_NOT_FOUND("0-003", "회사 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("0-004", "상품 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK("0-005", "상품 재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    COMPANY_NOT_MATCH("0-006", "회사 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_SERVER_ERROR("0-007", "상품 정보를 가져오는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}