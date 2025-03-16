package com.oingmaryho.business.orderservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<?> orderExceptionHandle(OrderException ex) {
        return ResponseEntity
            .status(ex.getErrorCode().getStatus())
            .body(ErrorMessage.of(ex.getErrorCode().toString(), ex.getMessage()));
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorMessage {
        String errorCode;
        String message;

        public static ErrorMessage of(String message, String errorCode) {
            return new ErrorMessage(errorCode, message);
        }
    }
}