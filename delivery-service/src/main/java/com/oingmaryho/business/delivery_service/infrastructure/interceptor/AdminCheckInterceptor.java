package com.oingmaryho.business.delivery_service.infrastructure.interceptor;

import com.oingmaryho.business.delivery_service.domain.UserConfirmStatus;
import com.oingmaryho.business.delivery_service.domain.UserRoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class AdminCheckInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("admin preHandle");
        Object userIdAttr = request.getAttribute("X-User-Id");

        if (userIdAttr == null) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE로 변경
        }

        String userId = String.valueOf(userIdAttr);

        if (!redisTemplate.hasKey("user:info:" + userId)) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE로 변경
        }

        Map<Object, Object> userInfo = redisTemplate.opsForHash().entries("user:info:" + userId);

        if (userInfo.isEmpty()) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE로 변경
        }

        if (!userInfo.get("status").toString().equals(UserConfirmStatus.CONFIRMED.toString())) {
            return false;
        }

        // 일반 사용자는 사용 불가
        if (!userInfo.get("role").toString().equals(UserRoleType.MASTER.toString())) {
            return false;
        }

        // 사용자 정보를 request에 주입
        request.setAttribute("userId", userId);
        request.setAttribute("username", userInfo.get("username"));
        request.setAttribute("slackId", userInfo.get("slackId"));
        request.setAttribute("role", userInfo.get("role"));

        return true;

    }
}
