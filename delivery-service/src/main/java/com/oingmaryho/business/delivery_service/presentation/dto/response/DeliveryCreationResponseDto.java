package com.oingmaryho.business.delivery_service.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record DeliveryCreationResponseDto(
        UUID orderId,
        UUID orderDetailId,
        UUID deliveryId,
        String deliveryDepartureName,
        List<String> deliveryStopoverName,
        String deliveryDestinationName,
        String deliveryManagerName){
}
