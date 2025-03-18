package com.oingmaryho.infrastructure.gateway_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.oingmaryho.infrastructure.gateway_service.filter.JwtAuthFilter;

@Configuration
public class GatewayConfig {

	@Autowired
	private JwtAuthFilter jwtAuthFilter;
}
