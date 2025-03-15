package com.oingmaryho.business.delivery_service.application.dto.request;

import java.util.UUID;

public class DeliveryUpdateRequestServiceDto {
    private UUID id;
    private String receiver;
    private String receiverSlackId;
    private String address;
    private Long managerId;
}
