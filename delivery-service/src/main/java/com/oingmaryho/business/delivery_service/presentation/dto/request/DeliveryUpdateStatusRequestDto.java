package com.oingmaryho.business.delivery_service.presentation.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;

public record DeliveryUpdateStatusRequestDto(DeliveryStatus status){
}
