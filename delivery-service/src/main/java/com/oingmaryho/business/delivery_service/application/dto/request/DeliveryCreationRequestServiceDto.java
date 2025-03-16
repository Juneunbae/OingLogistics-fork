package com.oingmaryho.business.delivery_service.application.dto.request;

import java.util.UUID;

public record DeliveryCreationRequestServiceDto (UUID orderId,
                                                 String address,
                                                 String receiver,
                                                 String receiverSlackId){

}
