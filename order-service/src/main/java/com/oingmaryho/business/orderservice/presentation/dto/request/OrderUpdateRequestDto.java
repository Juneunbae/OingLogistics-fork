package com.oingmaryho.business.orderservice.presentation.dto.request;

import java.util.List;

public record OrderUpdateRequestDto(
    String requests,
    List<OrderDetailUpdateRequestDto> requestOrderDetails
) {
}