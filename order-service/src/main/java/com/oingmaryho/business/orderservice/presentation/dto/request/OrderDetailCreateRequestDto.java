package com.oingmaryho.business.orderservice.presentation.dto.request;

import java.util.UUID;

public record OrderDetailCreateRequestDto(
    UUID recipientId,
    UUID productId,
    Integer quantity
) {
}