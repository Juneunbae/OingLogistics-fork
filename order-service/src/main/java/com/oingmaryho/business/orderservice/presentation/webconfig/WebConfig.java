package com.oingmaryho.business.orderservice.presentation.webconfig;


import com.oingmaryho.business.common.presentation.interceptor.AdminCheckInterceptor;
import com.oingmaryho.business.common.presentation.interceptor.UserCheckInterceptor;
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
        registry.addInterceptor(new UserCheckInterceptor(redisTemplate))
            .excludePathPatterns("/error", "/order-service/**");
        registry.addInterceptor(new AdminCheckInterceptor(redisTemplate))
            .excludePathPatterns("/api/**", "/order-service/**", "/error");
    }
}