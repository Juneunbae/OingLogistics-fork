package com.oingmaryho.business.delivery_service.application.dto.response;

import java.util.UUID;

public record DeliveryResponseServiceDto(UUID id,
                                         String status,
                                         UUID departureHubId,
                                         UUID destinationHubId,
                                         String address,
                                         String receiver,
                                         String receiverSlackId,
                                         UUID managerId){
}
