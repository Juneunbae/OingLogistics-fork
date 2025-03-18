package com.oingmaryho.business.delivery_service.domain;

import lombok.Getter;

@Getter
public enum DeliveryManagerType {


    HUB_DELIVERY_MANAGER("허브 배송담당자"),
    COMPANY_DELIVERY_MANAGER("업체 배송담당자");

    private final String description;

    DeliveryManagerType(final String description) { this.description = description; }
}
