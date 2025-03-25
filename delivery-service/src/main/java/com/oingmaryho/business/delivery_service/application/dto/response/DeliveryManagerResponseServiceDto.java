package com.oingmaryho.business.delivery_service.application.dto.response;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerResponseServiceDto(
        String slackId,
        UUID hubId,
        Long managerId,
        DeliveryManagerType type,
        Integer sequence,
        Boolean isDeleted
) {
}
