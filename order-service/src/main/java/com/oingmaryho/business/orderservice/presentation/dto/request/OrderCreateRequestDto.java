package com.oingmaryho.business.orderservice.presentation.dto.request;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequestDto(
    UUID requesterId,
    String requests,
    List<OrderDetailCreateRequestDto> orderDetailCreateDto
) {
}