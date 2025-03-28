package com.oingmaryho.business.orderservice.presentation.dto.response;

import java.util.UUID;

public record OrderDetailUpdateResponseDto(
    UUID id,
    UUID orderId,
    UUID recipientId,
    String recipientName,
    UUID recipientHubId,
    UUID productId,
    String productName,
    Integer quantity,
    Integer price
) {
}