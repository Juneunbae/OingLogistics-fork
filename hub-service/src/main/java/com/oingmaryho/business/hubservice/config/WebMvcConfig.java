package com.oingmaryho.business.hubservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.oingmaryho.business.common.presentation.interceptor.AdminCheckInterceptor;
import com.oingmaryho.business.common.presentation.interceptor.UserCheckInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final RedisTemplate<String, Object> redisTemplate;

	public WebMvcConfig(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new UserCheckInterceptor(redisTemplate))
			.excludePathPatterns("/hub-service/**", "/error");
		registry.addInterceptor(new AdminCheckInterceptor(redisTemplate))
			.excludePathPatterns("/hub-service/**", "/api/**", "/error");
	}

}