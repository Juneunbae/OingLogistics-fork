package com.oingmaryho.business.delivery_service.exception;

import lombok.Getter;

@Getter
public class DeliveryException extends RuntimeException {
    private final ErrorCode errorCode;

    public DeliveryException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}