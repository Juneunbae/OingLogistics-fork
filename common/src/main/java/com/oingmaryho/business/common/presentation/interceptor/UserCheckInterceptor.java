package com.oingmaryho.business.common.presentation.interceptor;

import java.util.Arrays;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.oingmaryho.business.common.domain.type.UserConfirmStatus;
import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.common.exception.BaseException;
import com.oingmaryho.business.common.exception.CommonErrorCode;
import com.oingmaryho.business.common.infrastructure.annotation.RequiredRoles;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserCheckInterceptor implements HandlerInterceptor {

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("user preHandle");
		String userId = request.getHeader("X-User-Id");

		if (userId == null) {
			return false;
			//throw new BaseException(CommonErrorCode.FORBIDDEN);    // TODO throw Exception
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

		if (!userInfo.get("status").toString().equals(UserConfirmStatus.CONFIRMED.toString())) {
			return false;   // TODO throw Exception
		}

		// 사용자 정보를 request에 주입
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

		if (handler instanceof HandlerMethod handlerMethod) {
			RequiredRoles requiredRoles = handlerMethod.getMethodAnnotation(RequiredRoles.class);
			if (requiredRoles != null) {
				boolean hasPermission = Arrays.stream(requiredRoles.value())
					.anyMatch(role -> role.equals(UserRoleType.valueOf((String) userInfo.get("role"))));
				System.out.println("권한 확인 : " + hasPermission);
				if (!hasPermission) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "권한이 없습니다.");
					return false;
				}
			}
		}
		return true;

	}
}