package com.oingmaryho.business.delivery_service.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean tryLock(String key, String value, long timeoutSeconds) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue()
                        .setIfAbsent(key, value, timeoutSeconds, TimeUnit.SECONDS)
        );
    }

    public void releaseLock(String key, String value) {
        Object currentValue = redisTemplate.opsForValue().get(key);
        if (value.equals(String.valueOf(currentValue))) {
            redisTemplate.delete(key);
        }
    }
}
