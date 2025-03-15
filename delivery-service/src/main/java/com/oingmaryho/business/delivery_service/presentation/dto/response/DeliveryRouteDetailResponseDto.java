package com.oingmaryho.business.delivery_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryRouteDetailResponseDto {
    private UUID id;
    private UUID deliveryId;
    private Integer sequence;
    private UUID departureHubId;
    private UUID destinationHubId;
    private String status;
    private Double estimatedDistance;
    private Integer estimatedTime;
    private Double actualDistance;
    private Integer actualTime;
    private Long managerId;
}
