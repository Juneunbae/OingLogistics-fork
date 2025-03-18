package com.oingmaryho.business.orderservice.application.dto.response;

import com.oingmaryho.business.orderservice.domain.Status;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDetailResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseServiceDto(
    UUID id,
    UUID recipientId,
    String recipientName,
    Status status,
    Integer totalPrice,
    String requests,
    Boolean isDeleted,
    Long deletedBy,
    LocalDateTime deletedAt,
    Long createdBy,
    LocalDateTime createdAt,
    Long updatedBy,
    LocalDateTime updatedAt,
    List<OrderDetailResponseDto> orderDetails
) {
}