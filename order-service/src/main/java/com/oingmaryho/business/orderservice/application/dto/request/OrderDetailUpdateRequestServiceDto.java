package com.oingmaryho.business.orderservice.application.dto.request;

public record OrderDetailUpdateRequestServiceDto(
    Integer price,
    Integer quantity
) {
}