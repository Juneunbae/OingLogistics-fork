package com.oingmaryho.business.delivery_service.presentation.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record DeliveryManagerSearchRequestDto(
        UUID id,
        String slackId,
        UUID hubId,
        Long managerId,
        DeliveryManagerType type,
        Integer sequence,
        Boolean isDeleted
) {
}

