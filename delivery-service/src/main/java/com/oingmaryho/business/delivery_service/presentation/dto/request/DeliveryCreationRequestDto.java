package com.oingmaryho.business.delivery_service.presentation.dto.request;

import java.util.UUID;

public record DeliveryCreationRequestDto(
        UUID orderId,               // 주문 id
        String requesterAddress,    // 배송지
        UUID requesterId,           // 수령인 id
        String requesterName,       // 수령인 이름
        String requesterSlackId,   // 수령인 slack id
        UUID orderDetailId,         // 상세 주문 id
        UUID recipientHubId,        // 상품 배송 담당하는 허브 id
        UUID recipientId,           // 상품 파는 업체 id
        String recipientName){      // 상품 파는 업체 이름
}
