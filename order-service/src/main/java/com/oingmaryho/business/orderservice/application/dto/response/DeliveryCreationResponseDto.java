package com.oingmaryho.business.orderservice.application.dto.response;

import java.util.UUID;

public record DeliveryCreationResponseDto(
    UUID orderId,
    UUID orderDetailId,
    UUID deliveryId,
    String deliveryDepartureName,
    String deliveryStopoverNames,
    String deliveryDestinationName,
    String deliveryManagerName,
    String deliveryManagerSlackId
) {
}