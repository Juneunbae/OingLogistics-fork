package com.oingmaryho.business.delivery_service.application.dto.response;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;

import java.util.UUID;

public record DeliveryRouteResponseServiceDto(UUID id,
                                              UUID deliveryId,
                                              Integer sequence,
                                              UUID departureHubId,
                                              UUID destinationHubId,
                                              DeliveryRouteStatus status,
                                              Double estimatedDistance,
                                              Integer estimatedTime,
                                              Double actualDistance,
                                              Integer actualTime,
                                              UUID managerId,
                                              Boolean isDeleted){

}
