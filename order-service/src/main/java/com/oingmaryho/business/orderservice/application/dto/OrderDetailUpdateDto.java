package com.oingmaryho.business.orderservice.application.dto;

public record OrderDetailUpdateDto(
    Integer price,
    Integer quantity
) {
}