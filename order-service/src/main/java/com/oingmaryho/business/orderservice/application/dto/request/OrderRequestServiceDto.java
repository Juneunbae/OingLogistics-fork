package com.oingmaryho.business.orderservice.application.dto.request;

import java.util.UUID;

public record OrderRequestServiceDto(
    UUID orderId
) {
}