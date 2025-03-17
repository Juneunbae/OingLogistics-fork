package com.oingmaryho.business.hubservice.config.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    HUB("hub", 300, 500),
    HUBS("hubs", 300, 500);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}