package com.oringmaryho.business.slackservice.config.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    USER("user", 300, 500),
    USERS("users", 300, 500);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}