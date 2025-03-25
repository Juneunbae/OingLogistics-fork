package com.oingmaryho.business.delivery_service.presentation.dto.response;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerAdminResponseDto(
        String slackId,
        UUID hubId,
        Long managerId,
        DeliveryManagerType type,
        Integer sequence,
        Boolean isDeleted
) {
}
