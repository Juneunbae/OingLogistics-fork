package com.oingmaryho.business.delivery_service.infrastructure.interceptor;

import com.oingmaryho.business.delivery_service.domain.type.UserConfirmStatus;
import com.oingmaryho.business.delivery_service.domain.type.UserRoleType;
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
//
//        if (request.getAttribute("status") == null) {
//            return false;    // TODO throw Exception
//        }
//
//        if (!request.getAttribute("status").toString().equals(UserConfirmStatus.CONFIRMED.toString())) {
//            return false;   // TODO throw Exception
//        }
//
//        if (request.getAttribute("role") == null) {
//            return false;   // TODO throw Exception
//        }
//
//        if (!request.getAttribute("role").toString().equals(UserRoleType.MASTER.toString())) {
//            return false;   // TODO throw Exception
//        }

        return true;

    }
}
