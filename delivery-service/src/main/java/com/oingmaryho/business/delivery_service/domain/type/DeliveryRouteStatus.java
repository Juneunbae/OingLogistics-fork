package com.oingmaryho.business.delivery_service.domain.type;

import lombok.Getter;

@Getter
public enum DeliveryRouteStatus {
    HUB_WAITING("허브 대기중"),
    HUB_MOVING("허브 이동중"),
    HUB_ARRIVED("목적지 허브 도착");

    private final String description;

    DeliveryRouteStatus(String description) { this.description = description; }

}
