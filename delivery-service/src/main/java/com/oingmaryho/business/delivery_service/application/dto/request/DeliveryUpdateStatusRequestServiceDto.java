package com.oingmaryho.business.delivery_service.application.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;

import java.util.UUID;

public record DeliveryUpdateStatusRequestServiceDto(UUID id,
                                                   DeliveryStatus status){
}
