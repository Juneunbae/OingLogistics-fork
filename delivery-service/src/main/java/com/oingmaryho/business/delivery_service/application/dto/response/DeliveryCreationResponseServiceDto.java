package com.oingmaryho.business.delivery_service.application.dto.response;

import java.util.List;
import java.util.UUID;

public record DeliveryCreationResponseServiceDto(
        UUID orderId,
        UUID orderDetailId,
        UUID deliveryId,
        String deliveryDepartureName,
        List<String> deliveryStopoverName,
        String deliveryDestinationName,
        String deliveryManagerName){
}
