package com.oingmaryho.business.orderservice.application.dto;

import org.springframework.data.domain.Pageable;

public record OrdersServiceDto(
    String productName,
    String recipientName,
    String requesterName,
    Boolean isDeleted,
    Pageable customPageable
) {
}