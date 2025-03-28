package com.oingmaryho.business.orderservice.application.dto.response;

import com.oingmaryho.business.orderservice.domain.Status;

import java.util.List;
import java.util.UUID;

public record OrderCreateResponseServiceDto(
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
    List<OrderDetailCreateResponseDto> orderDetails
) {
}