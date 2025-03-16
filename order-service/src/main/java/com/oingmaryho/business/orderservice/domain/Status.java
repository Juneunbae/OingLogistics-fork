package com.oingmaryho.business.orderservice.domain;

import lombok.Getter;

@Getter
public enum Status {
    ORDERING("주문 중"),
    COMPLETE("주문 완료");

    private final String description;

    Status(String description) {
        this.description = description;
    }
}