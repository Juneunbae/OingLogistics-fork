package com.oingmaryho.business.orderservice.presentation.dto.response;

import java.util.UUID;

public record OrderDetailDto(
    UUID id,
    UUID orderId,
    UUID requesterId,
    String requesterName,
    UUID shippingId,
    UUID productId,
    String productName,
    Integer quantity,
    Integer price
) {
}