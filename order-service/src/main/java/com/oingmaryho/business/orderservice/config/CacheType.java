package com.oingmaryho.business.orderservice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    ORDER("order", 300, 500),
    ORDERS("orders", 300, 500);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}