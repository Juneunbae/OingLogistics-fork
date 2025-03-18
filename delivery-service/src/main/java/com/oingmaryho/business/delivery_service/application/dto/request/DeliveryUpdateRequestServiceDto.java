package com.oingmaryho.business.delivery_service.application.dto.request;

import java.util.UUID;

public record DeliveryUpdateRequestServiceDto(UUID id,
                                             String receiver,
                                             String receiverSlackId,
                                             String address,
                                             UUID managerId){
}
