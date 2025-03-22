package com.oingmaryho.business.orderservice.application.dto.request;

import java.util.UUID;

public record OrderDetailCreateRequestServiceDto(
    UUID recipientId,
    UUID productId,
    Integer quantity
) {
}