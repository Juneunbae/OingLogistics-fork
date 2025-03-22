package com.oingmaryho.business.delivery_service.presentation.dto.response;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;

import java.util.UUID;

public record DeliveryRouteAdminResponseDto(
        UUID id,
        UUID deliveryId,
        Integer sequence,
        UUID departureHubId,
        UUID arriveHubId,
        DeliveryRouteStatus status,
        Double estimatedDistance,
        Integer estimatedTime,
        Double actualDistance,
        Integer actualTime,
        UUID managerId,
        Boolean isDeleted) {
}
