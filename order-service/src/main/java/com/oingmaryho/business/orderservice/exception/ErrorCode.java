package com.oingmaryho.business.orderservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND("O-001", "주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_DETAIL_NOT_FOUND("0-002", "상세 주문 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}