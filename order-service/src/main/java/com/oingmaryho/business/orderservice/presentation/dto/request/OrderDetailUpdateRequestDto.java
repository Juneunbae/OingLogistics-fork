package com.oingmaryho.business.orderservice.presentation.dto.request;

import java.util.UUID;

public record OrderDetailUpdateRequestDto(
    UUID orderDetailId,
    Integer quantity,
    Integer price
) {
}