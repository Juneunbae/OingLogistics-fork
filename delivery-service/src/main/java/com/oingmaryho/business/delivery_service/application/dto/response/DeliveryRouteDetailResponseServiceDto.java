package com.oingmaryho.business.delivery_service.application.dto.response;

import java.util.UUID;

public record DeliveryRouteDetailResponseServiceDto(UUID id,
                                                    UUID deliveryId,
                                                    Integer sequence,
                                                    UUID departureHubId,
                                                    UUID destinationHubId,
                                                    String status,
                                                    Double estimatedDistance,
                                                    Integer estimatedTime,
                                                    Double actualDistance,
                                                    Integer actualTime,
                                                    Long managerId){
}
