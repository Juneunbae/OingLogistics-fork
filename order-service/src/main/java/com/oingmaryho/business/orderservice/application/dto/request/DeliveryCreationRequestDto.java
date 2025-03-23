package com.oingmaryho.business.orderservice.application.dto.request;

import java.util.UUID;

public record DeliveryCreationRequestDto(
    UUID orderId,
    UUID requesterId,
    String requesterName,
    String requesterAddress,
    String requesterSlackId,
    UUID orderDetailId,
    UUID recipientId,
    String recipientName,
    UUID recipientHubId
) {
}