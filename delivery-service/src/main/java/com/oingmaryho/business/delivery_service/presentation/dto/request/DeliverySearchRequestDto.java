package com.oingmaryho.business.delivery_service.presentation.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;

import java.util.UUID;

public record DeliverySearchRequestDto(
        UUID id,
        UUID orderId,
        UUID hubId,
        UUID companyId,
        DeliveryStatus status,
        Long managerId,
        Boolean isDeleted) {
}