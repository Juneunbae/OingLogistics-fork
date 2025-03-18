package com.oingmaryho.business.delivery_service.presentation.dto.response;

import java.util.UUID;

public record DeliveryRouteResponseDto(UUID id,
                                       UUID deliveryId,
                                       Integer sequence,
                                       UUID departureHubId,
                                       UUID destinationHubId,
                                       String status,
                                       Double estimatedDistance,
                                       Integer estimatedTime,
                                       Double actualDistance,
                                       Integer actualTime,
                                       UUID managerId){
}
