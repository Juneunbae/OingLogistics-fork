package com.oingmaryho.business.companyservice.config.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    COMPANY("company", 300, 500),
    ;
    public static final String COMPANY_CACHE = "company";
    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}