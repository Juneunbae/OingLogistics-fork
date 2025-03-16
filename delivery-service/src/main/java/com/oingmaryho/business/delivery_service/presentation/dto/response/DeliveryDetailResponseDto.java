package com.oingmaryho.business.delivery_service.presentation.dto.response;

import java.util.UUID;

public record DeliveryDetailResponseDto(UUID id,
                                        String status,
                                        UUID departureHubId,
                                        String departureHubName,
                                        UUID destinationHubId,
                                        String destinationHubName,
                                        String address,
                                        String receiver,
                                        String receiverSlackId,
                                        UUID managerId){
}
