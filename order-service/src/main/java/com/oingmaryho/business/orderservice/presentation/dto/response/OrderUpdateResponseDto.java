package com.oingmaryho.business.orderservice.presentation.dto.response;

import com.oingmaryho.business.orderservice.domain.Status;

import java.util.List;
import java.util.UUID;

public record OrderUpdateResponseDto(
    UUID id,
    UUID requesterId,
    String requesterSlackId,
    String requesterName,
    String requesterAddress,
    Long requesterUserId,
    String requesterUsername,
    Status status,
    String requests,
    Integer totalPrice,
    List<OrderDetailUpdateResponseDto> orderDetails
) {
}