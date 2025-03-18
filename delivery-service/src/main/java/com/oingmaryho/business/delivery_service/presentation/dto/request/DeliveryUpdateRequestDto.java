package com.oingmaryho.business.delivery_service.presentation.dto.request;

import java.util.UUID;

public record DeliveryUpdateRequestDto(String receiver,
                                       String receiverSlackId,
                                       String address,
                                       UUID managerId){
}