package com.oingmaryho.business.orderservice.application.dto.request;

public record OrderUpdateRequestServiceDto(
    String requests,
    Integer totalPrice
) {
}