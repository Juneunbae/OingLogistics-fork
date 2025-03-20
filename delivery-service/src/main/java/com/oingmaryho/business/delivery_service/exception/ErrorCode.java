package com.oingmaryho.business.delivery_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DELIVERY_NOT_FOUND("D-001", "해당 배송을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ROUTE_NOT_FOUND("D-002", "해당 배송 경로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MANGER_NOT_FOUND("D-003", "해당 배송 담당자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    UNAUTHORIZED("D-004", "해당 요청에 대한 권한이 없습니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;

}
