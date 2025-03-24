package com.oingmaryho.business.delivery_service.application.dto.response;

import java.util.UUID;

public record DeliveryCreationResponseServiceDto(
        UUID orderId,
        UUID orderDetailId,
        UUID deliveryId){
}
