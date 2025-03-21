package com.oingmaryho.business.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class Auditing {
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return Optional.empty();
            }

            String userId = attributes.getRequest().getHeader("userId");

            if (userId != null) {
                try {
                    return Optional.of(Long.parseLong(userId));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            }

            return Optional.empty();
        };
    }
}