package com.oingmaryho.business.orderservice.application.dto;

import java.util.UUID;

public record OrderDetailUpdateServiceDto(
    UUID orderDetailId,
    Integer quantity,
    Integer price
) {
}