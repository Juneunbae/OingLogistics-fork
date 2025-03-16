package com.oingmaryho.business.orderservice.presentation.dto.request;

public record OrderSearchRequestDto(
    String productName,
    String recipientName,
    String requesterName,
    Boolean isDeleted
) {
}