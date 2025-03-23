package com.oingmaryho.business.common.presentation.interceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		IOException {
		try {
			log.info("user preHandle");

			String userIdAttr = request.getHeader("X-User-Id");
			if (userIdAttr == null) {
				throw new BaseException(CommonErrorCode.UNAUTHORIZED);
			}

			long userId = Long.parseLong(userIdAttr);
			String redisKey = "user:info:" + userId;

			if (!redisTemplate.hasKey(redisKey)) {
				throw new BaseException(CommonErrorCode.USER_NOT_FOUND);
			}

			Map<Object, Object> userInfo = redisTemplate.opsForHash().entries(redisKey);
			if (userInfo.isEmpty()) {
				throw new BaseException(CommonErrorCode.USER_NOT_FOUND);
			}

			if (!UserConfirmStatus.CONFIRMED.name().equals(userInfo.get("status"))) {
				throw new BaseException(CommonErrorCode.INVALID_USER_STATUS);
			}

			if (!userInfo.containsKey("username") || !userInfo.containsKey("slackId") || !userInfo.containsKey("role")) {
				throw new BaseException(CommonErrorCode.MISSING_USER_INFO);
			}

			// request에 사용자 정보 주입
			request.setAttribute("userId", userId);
			request.setAttribute("username", userInfo.get("username"));
			request.setAttribute("slackId", userInfo.get("slackId"));
			request.setAttribute("role", userInfo.get("role"));

			// @RequiredRoles 체크
			if (handler instanceof HandlerMethod handlerMethod) {
				RequiredRoles requiredRoles = handlerMethod.getMethodAnnotation(RequiredRoles.class);
				if (requiredRoles != null) {
					boolean hasPermission = Arrays.stream(requiredRoles.value())
						.anyMatch(role -> role.equals(UserRoleType.valueOf((String) userInfo.get("role"))));
					if (!hasPermission) {
						throw new BaseException(CommonErrorCode.FORBIDDEN);
					}
				}
			}

			return true;

		} catch (BaseException ex) {
			response.setStatus(ex.getErrorCode().getStatus().value());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			String body = objectMapper.writeValueAsString(ErrorResponse.of(
				ex.getErrorCode().getErrorCode(),
				ex.getErrorCode().getMessage()
			));
			response.getWriter().write(body);
			return false;
		}
	}
	private record ErrorResponse(String errorCode, String message) {
		static ErrorResponse of(String errorCode, String message) {
			return new ErrorResponse(errorCode, message);
		}
	}
}