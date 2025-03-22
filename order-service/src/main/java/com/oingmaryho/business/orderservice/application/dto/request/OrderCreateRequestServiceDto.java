package com.oingmaryho.business.orderservice.application.dto.request;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequestServiceDto(
    UUID requesterId,
    String requests,
    List<OrderDetailCreateRequestServiceDto> orderDetails
) {
}