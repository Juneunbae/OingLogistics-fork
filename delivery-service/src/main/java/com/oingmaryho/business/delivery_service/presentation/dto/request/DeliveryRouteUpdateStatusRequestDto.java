package com.oingmaryho.business.delivery_service.presentation.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;

public record DeliveryRouteUpdateStatusRequestDto(DeliveryRouteStatus status) {
}
