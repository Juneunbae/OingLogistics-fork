package com.oingmaryho.business.delivery_service.presentation.dto.request;

public record DeliveryUpdateRequestDto(String receiver,
                                       String receiverSlackId,
                                       String address,
                                       Long managerId){
}