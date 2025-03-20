package com.oingmaryho.business.delivery_service.presentation.dto.response;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;

import java.util.UUID;

public record DeliveryResponseDto(UUID id,
                                  DeliveryStatus status,
                                  UUID departureHubId,
                                  UUID destinationHubId,
                                  String address,
                                  String receiver,
                                  String receiverSlackId,
                                  UUID managerId){
}
