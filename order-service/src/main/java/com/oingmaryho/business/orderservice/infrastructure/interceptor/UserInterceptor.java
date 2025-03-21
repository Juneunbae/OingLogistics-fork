package com.oingmaryho.business.orderservice.infrastructure.interceptor;

import com.oingmaryho.business.orderservice.domain.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("user preHandle");
        String userId = request.getHeader("X-User-Id");

        if (userId == null) {
            return false;    // TODO throw Exception
        }

        if (!redisTemplate.hasKey("user:info:" + userId)) {
            return false;    // TODO throw Exception
        }

        Map<Object, Object> userInfo = redisTemplate.opsForHash().entries("user:info:" + userId);
        if (userInfo.isEmpty()) {
            return false;    // TODO throw Exception
        }

        if (!userInfo.containsKey("status")) {
            return false;   // TODO throw Exception
        }

        if (!userInfo.get("status").equals(UserStatus.CONFIRMED)) {
            return false;   // TODO throw Exception
        }

        // 사용자 정보를 request 에 주입
        request.setAttribute("userId", userId);

        if (!userInfo.containsKey("username")) {
            return false;   // TODO throw Exception
        }
        request.setAttribute("username", userInfo.get("username"));

        if (!userInfo.containsKey("slackId")) {
            return false;   // TODO throw Exception
        }
        request.setAttribute("slackId", userInfo.get("slackId"));

        if (!userInfo.containsKey("role")) {
            return false;   // TODO throw Exception
        }
        request.setAttribute("role", userInfo.get("role"));

        return true;
    }
}