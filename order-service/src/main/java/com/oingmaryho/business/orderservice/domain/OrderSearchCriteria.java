package com.oingmaryho.business.orderservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSearchCriteria {
    private final String productName;
    private final String recipientName;
    private final String requesterName;
    private final Boolean isDeleted;
}