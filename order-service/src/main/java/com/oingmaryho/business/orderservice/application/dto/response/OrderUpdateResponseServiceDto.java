package com.oingmaryho.business.orderservice.application.dto.response;

import java.util.List;
import java.util.UUID;

public record OrderUpdateResponseServiceDto(
    UUID id,
    String requests,
    List<OrderDetailUpdateResponseServiceDto> orderDetails
) {
}