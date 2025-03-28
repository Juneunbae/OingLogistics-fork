package com.oingmaryho.business.orderservice.application.dto.response;

import java.util.UUID;

public record OrderDetailCreateResponseServiceDto(
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