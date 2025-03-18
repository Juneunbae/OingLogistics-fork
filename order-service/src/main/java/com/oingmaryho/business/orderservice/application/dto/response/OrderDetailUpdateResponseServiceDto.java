package com.oingmaryho.business.orderservice.application.dto.response;

import java.util.UUID;

public record OrderDetailUpdateResponseServiceDto(
    UUID orderDetailId,
    Integer quantity,
    Integer price
) {
}