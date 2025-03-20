package com.oingmaryho.business.delivery_service.application.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;

import java.util.UUID;

public record DeliveryRouteUpdateStatusRequestServiceDto(UUID id,
                                                         DeliveryRouteStatus status) {
}
