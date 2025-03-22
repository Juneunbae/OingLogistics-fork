package com.oingmaryho.business.delivery_service.presentation.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;

import java.util.UUID;

public record DeliveryRouteSearchRequestDto(
        UUID routeId,
        UUID departureHubId,
        UUID arriveHubId,
        UUID companyId,
        Long managerId,
        DeliveryRouteStatus status,
        Boolean isDeleted){
}
