package com.oingmaryho.business.delivery_service.presentation.dto.request;

import lombok.Getter;

@Getter
public class DeliveryUpdateRequestDto {
    private String receiver;
    private String receiverSlackId;
    private String address;
    private Long managerId;
}