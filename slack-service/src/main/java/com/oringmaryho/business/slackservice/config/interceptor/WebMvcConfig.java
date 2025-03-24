package com.oringmaryho.business.slackservice.config.interceptor;

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
        .excludePathPatterns("/api/**");

    // 일반 사용자 확인용 인터셉터
    registry.addInterceptor(new UserCheckInterceptor(redisTemplate))
        .excludePathPatterns("/admin/**");
  }

}