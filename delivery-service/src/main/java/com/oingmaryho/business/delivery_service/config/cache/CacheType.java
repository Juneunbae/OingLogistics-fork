package com.oingmaryho.business.delivery_service.config.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    DELIVERY("delivery", 300, 500),
    DELIVERIES("deliveries", 300, 500),
    ROUTE("route", 300, 500),
    ROUTES("routes", 300, 500);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}