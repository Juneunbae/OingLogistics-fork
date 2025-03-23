package com.oingmaryho.business.orderservice.presentation.dto.request;

import org.springframework.data.domain.Pageable;

public record OrderAdminRequestServiceDto(
    String productName,
    String recipientName,
    String requesterName,
    Boolean isDeleted,
    Pageable customPageable
) {
}