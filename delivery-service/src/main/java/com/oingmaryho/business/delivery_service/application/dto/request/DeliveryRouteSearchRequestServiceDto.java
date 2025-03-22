package com.oingmaryho.business.delivery_service.application.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record DeliveryRouteSearchRequestServiceDto(
        UUID routeId,
        UUID deliveryId,
        UUID departureHubId,
        UUID arriveHubId,
        UUID companyId,
        Long managerId,
        DeliveryRouteStatus status,
        Boolean isDeleted,
        Pageable customPageable){
}
