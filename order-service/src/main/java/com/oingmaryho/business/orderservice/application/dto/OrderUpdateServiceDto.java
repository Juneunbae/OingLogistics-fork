package com.oingmaryho.business.orderservice.application.dto;

import java.util.List;
import java.util.UUID;

public record OrderUpdateServiceDto(
    UUID id,
    String requests,
    List<OrderDetailUpdateServiceDto> orderDetails
) {
}