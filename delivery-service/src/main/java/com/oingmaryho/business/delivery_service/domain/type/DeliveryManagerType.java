package com.oingmaryho.business.delivery_service.domain.type;

import lombok.Getter;

@Getter
public enum DeliveryManagerType {


    HUB_DELIVERY_MANAGER("허브 배송담당자"),
    COMPANY_DELIVERY_MANAGER("업체 배송담당자");

    private final String description;

    DeliveryManagerType(final String description) { this.description = description; }

    // UserRoleType에서 변환하는 메서드
    public static DeliveryManagerType fromUserRoleType(UserRoleType role) {

        if (role.name().equals(HUB_DELIVERY_MANAGER.name()) || role.name().equals(COMPANY_DELIVERY_MANAGER.name())) {
            return DeliveryManagerType.valueOf(role.name());
        }

        return null;

    }
}
