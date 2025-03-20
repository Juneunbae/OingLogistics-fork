package com.oingmaryho.business.delivery_service.presentation.dto.response;

import java.util.UUID;

public record DeliveryAdminResponseDto(UUID id,
                                       String status,
                                       UUID departureHubId,
                                       UUID destinationHubId,
                                       String address,
                                       String receiver,
                                       String receiverSlackId,
                                       UUID managerId,
                                       Boolean isDeleted){
}
