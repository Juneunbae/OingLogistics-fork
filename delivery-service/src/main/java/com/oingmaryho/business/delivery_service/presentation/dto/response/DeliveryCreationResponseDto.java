package com.oingmaryho.business.delivery_service.presentation.dto.response;

import java.util.UUID;

public record DeliveryCreationResponseDto(
        UUID orderId,
        UUID orderDetailId,
        UUID deliveryId){
}
