package com.oingmaryho.business.orderservice.presentation.webconfig;

import com.oingmaryho.business.orderservice.infrastructure.interceptor.AdminInterceptor;
import com.oingmaryho.business.orderservice.infrastructure.interceptor.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor(redisTemplate))
            .excludePathPatterns("/order-service/**");
        registry.addInterceptor(new AdminInterceptor(redisTemplate))
            .excludePathPatterns("/api/**")
            .excludePathPatterns("/order-service/**");
    }
}