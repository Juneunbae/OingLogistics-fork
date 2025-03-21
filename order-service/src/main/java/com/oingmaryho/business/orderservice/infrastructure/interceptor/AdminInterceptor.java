package com.oingmaryho.business.orderservice.infrastructure.interceptor;

import com.oingmaryho.business.orderservice.domain.UserRole;
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
public class AdminInterceptor implements HandlerInterceptor {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("admin preHandle");
        Object userIdAttr = request.getAttribute("X-User-Id");

        if (userIdAttr == null) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE
        }

        String userId = String.valueOf(userIdAttr);

        if (!redisTemplate.hasKey("user:info:" + userId)) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE
        }

        Map<Object, Object> userInfo = redisTemplate.opsForHash().entries("user:info:" + userId);

        if (userInfo.isEmpty()) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE
        }

        if (!userInfo.get("status").equals(UserStatus.CONFIRMED)) {
            return false;
        }

        if (!userInfo.get("role").equals(UserRole.MASTER)) {
            return false;
        }

        request.setAttribute("userId", userId);
        request.setAttribute("username", userInfo.get("username"));
        request.setAttribute("slackId", userInfo.get("slackId"));
        request.setAttribute("role", userInfo.get("role"));

        return true;
    }
}