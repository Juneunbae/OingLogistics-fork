package com.oingmaryho.business.orderservice.domain;

import lombok.Getter;

@Getter
public enum UserStatus {
    PENDING("미 인증"),
    CONFIRMED("인증"),
    FAILED("인증 실패");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }
}