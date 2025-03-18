package com.oingmaryho.infrastructure.gateway_service.config;

import com.oingmaryho.infrastructure.gateway_service.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

  @Bean
  public GlobalFilter jwtAuthFilter() {
    return new JwtAuthFilter();
  }
}
