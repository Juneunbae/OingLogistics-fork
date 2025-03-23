package com.oingmaryho.business.productservice.config.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    PRODUCT("product", 300, 500),
    ;
    public static final String PRODUCT_CACHE = "product";
    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}