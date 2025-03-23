package com.oingmaryho.business.common.presentation.interceptor;

import java.util.Map;

import com.oingmaryho.business.common.domain.type.UserConfirmStatus;
import com.oingmaryho.business.common.domain.type.UserRoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;



@Slf4j
@RequiredArgsConstructor
public class AdminCheckInterceptor implements HandlerInterceptor {
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("admin preHandle");
		String userIdAttr = request.getHeader("X-User-Id");
		log.info("userIdAttr: {}", userIdAttr);

		if (userIdAttr == null) {
			return false;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE
		}

		long userId = Long.parseLong(userIdAttr);

		if (!redisTemplate.hasKey("user:info:" + userId)) {
			return false;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE
		}

		Map<Object, Object> userInfo = redisTemplate.opsForHash().entries("user:info:" + userId);

		if (userInfo.isEmpty()) {
			return false;    // TODO 테스트 끝난 후 로그인 기능 구현되면 FALSE
		}

		if (!userInfo.get("status").equals(UserConfirmStatus.CONFIRMED.name())) {
			return false;
		}

		if (!userInfo.get("role").equals(UserRoleType.MASTER.name())) {
			return false;
		}

		// 사용자 정보를 request 에 주입
		request.setAttribute("userId", userId);
		log.info("주입");

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

		log.info("admin interceptor 완료");
		return true;
	}
}