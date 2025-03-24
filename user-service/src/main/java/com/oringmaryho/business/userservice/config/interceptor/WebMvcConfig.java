package com.oringmaryho.business.userservice.config.interceptor;

import com.oingmaryho.business.common.presentation.interceptor.AdminCheckInterceptor;
import com.oingmaryho.business.common.presentation.interceptor.UserCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final RedisTemplate<String, Object> redisTemplate;

	public WebMvcConfig(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// admin 확인용 인터셉터
		registry.addInterceptor(new AdminCheckInterceptor(redisTemplate))
			.excludePathPatterns(
					"/api/**",
					"/admin/v1/users/slack/confirm-code",
					"/admin/v1/users/slack/confirm",
					"/admin/v1/users/sign-in",
					"/admin/v1/users/sign-out",
					"/user-service/users/**",
					"/error/**"
			);

		// 일반 사용자 확인용 인터셉터
		registry.addInterceptor(new UserCheckInterceptor(redisTemplate))
			.excludePathPatterns(
					"/admin/**",
					"/api/v1/users/slack/confirm-code",
					"/api/v1/users/slack/confirm",
					"/api/v1/users/sign-in",
					"/api/v1/users/sign-out",
					"/user-service/users/**",
					"/error/**"
			);
	}

}