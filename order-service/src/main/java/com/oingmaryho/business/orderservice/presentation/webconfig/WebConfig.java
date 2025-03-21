package com.oingmaryho.business.orderservice.presentation.webconfig;

import com.oingmaryho.business.orderservice.infrastructure.interceptor.AdminInterceptor;
import com.oingmaryho.business.orderservice.infrastructure.interceptor.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AdminInterceptor adminInterceptor;
    private final UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor);
        registry.addInterceptor(userInterceptor);
    }
}