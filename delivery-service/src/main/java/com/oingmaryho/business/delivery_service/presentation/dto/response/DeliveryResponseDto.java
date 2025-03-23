package com.oingmaryho.business.delivery_service.presentation.dto.response;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;

import java.util.UUID;

public record DeliveryResponseDto(
        UUID id,
        UUID orderId,
        UUID orderDetailId,
        UUID companyId,
        DeliveryStatus status,
        UUID departureHubId,
        UUID arriveHubId,
        String address,
        String receiver,
        String receiverSlackId,
        UUID managerId){
}
