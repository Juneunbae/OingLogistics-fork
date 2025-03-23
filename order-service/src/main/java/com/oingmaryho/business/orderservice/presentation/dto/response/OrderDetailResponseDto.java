package com.oingmaryho.business.orderservice.presentation.dto.response;

import java.util.UUID;

public record OrderDetailResponseDto(
    UUID id,
    UUID orderId,
    UUID recipientId,
    String recipientName,
    UUID recipientHubId,
    UUID deliveryId,
    UUID productId,
    String productName,
    Integer quantity,
    Integer price
) {
}