package com.oingmaryho.business.delivery_service.domain;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    HUB_WAITING("허브 대기중"),
    HUB_MOVING("허브 이동중"),
    HUB_ARRIVED("목적지 허브 도착"),
    COMPANY_MOVING("업체 배송중"),
    COMPLETE("배송완료");

    private final String description;

    DeliveryStatus(String description) { this.description = description; }
}
