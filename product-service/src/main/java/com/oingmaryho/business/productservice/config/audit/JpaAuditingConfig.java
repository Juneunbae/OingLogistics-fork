package com.oingmaryho.business.productservice.config.audit;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@EnableJpaAuditing
@Configuration
public class JpaAuditingConfig {

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