package com.oingmaryho.business.orderservice.application.dto.request;

import org.springframework.data.domain.Pageable;

public record OrdersRequestServiceDto(
    String productName,
    String recipientName,
    String requesterName,
    Boolean isDeleted,
    Pageable customPageable
) {
}