package com.oingmaryho.business.delivery_service.presentation.dto.request;

import java.util.UUID;

public record DeliveryCreationRequestDto(UUID orderId,
                                         String address,
                                         String receiver,
                                         String receiverSlackId){
}
