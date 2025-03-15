package com.oingmaryho.business.delivery_service.presentation.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryCreationRequestDto {
    private UUID orderId;
    private String address;
    private String receiver;
    private String receiverSlackId;
}
