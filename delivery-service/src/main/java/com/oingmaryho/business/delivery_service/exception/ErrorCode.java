package com.oingmaryho.business.delivery_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DELIVERY_NOT_FOUND("D-001", "해당 배송을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ROUTE_NOT_FOUND("D-002", "해당 배송 경로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MANAGER_NOT_FOUND("D-003", "해당 배송 담당자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    UNAUTHORIZED("D-004", "해당 요청에 대한 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("D-005", "해당 요청에 대한 권한이 없습니다.", HttpStatus.BAD_REQUEST),

    HUB_NOT_FOUND("H-001", "배송 정보 확인 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    HUB_ROUTE_NOT_FOUND("H-002", "배송 경로 생성 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COMPANY_NOT_FOUND("C-001", "업체 조회 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

}
