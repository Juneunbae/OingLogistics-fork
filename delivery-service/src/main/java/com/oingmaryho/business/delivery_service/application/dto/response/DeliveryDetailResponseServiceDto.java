package com.oingmaryho.business.delivery_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryDetailResponseServiceDto {
    private UUID id;
    private String status;
    private UUID departureHubId;
    private String departureHubName;
    private UUID destinationHubId;
    private String destinationHubName;
    private String address;
    private String receiver;
    private String receiverSlackId;
    private UUID managerId;
}
