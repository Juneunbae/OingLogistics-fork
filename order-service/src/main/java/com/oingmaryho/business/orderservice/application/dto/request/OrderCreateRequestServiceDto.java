package com.oingmaryho.business.orderservice.application.dto.request;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequestServiceDto(
    Long userId,
    String username,
    String slackId,
    UUID requesterId,
    String requests,
    List<OrderDetailCreateRequestServiceDto> orderDetails
) {
}