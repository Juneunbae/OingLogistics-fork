package com.oringmaryho.business.userservice.infrastructure.interceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.UserRoleType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AdminCheckInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;
    // 인증 제외 경로 정의
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/admin/v1/users/slack/confirm-code",
        "/admin/v1/users/slack/confirm",
        "/admin/v1/users/sign-out"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("admin preHandle");
        Object userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader == null) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE로 변경
        }

        String userId = String.valueOf(userIdHeader);
        String requestPath = request.getRequestURI();

        if (!redisTemplate.hasKey("user:info:" + userId)) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE로 변경
        }

        Map<Object, Object> userInfo = redisTemplate.opsForHash().entries("user:info:" + userId);

        if (userInfo.isEmpty()) {
            return true;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE로 변경
        }
        // 제외 경로에 해당하면 바로 true 반환
        if (EXCLUDED_PATHS.contains(requestPath)) {
            log.info("Skipping authentication for path: {}", requestPath);
            // 사용자 정보를 request에 주입
            request.setAttribute("userId", userId);
            request.setAttribute("username", userInfo.get("username"));
            request.setAttribute("slackId", userInfo.get("slackId"));
            request.setAttribute("role", userInfo.get("role"));
            return true;
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
