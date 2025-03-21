package com.oingmaryho.business.delivery_service.application.dto.response;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;

import java.util.UUID;

public record DeliveryResponseServiceDto(UUID id,
                                         DeliveryStatus status,
                                         UUID departureHubId,
                                         UUID arriveHubId,
                                         String address,
                                         String receiver,
                                         String receiverSlackId,
                                         UUID managerId,
                                         Boolean isDeleted){
}
