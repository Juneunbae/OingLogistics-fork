package com.oingmaryho.business.delivery_service.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryCreationRequestServiceDto {
    private UUID orderId;
    private String address;
    private String receiver;
    private String receiverSlackId;
}
