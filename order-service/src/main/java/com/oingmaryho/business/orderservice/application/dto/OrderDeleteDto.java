package com.oingmaryho.business.orderservice.application.dto;

import java.util.UUID;

public record OrderDeleteDto(
    UUID orderId
) {
}