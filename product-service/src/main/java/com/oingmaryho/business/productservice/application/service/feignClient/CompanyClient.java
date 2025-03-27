package com.oingmaryho.business.productservice.application.service.feignClient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.oingmaryho.business.productservice.presentation.dto.response.CompanyDetailsSearchResponseDto;

@FeignClient(name = "company-service")
public interface CompanyClient {

	@GetMapping("/company-service/companies/{id}")
	Optional<CompanyDetailsSearchResponseDto> getCompanyById(
		@PathVariable UUID id
	);

	@GetMapping("/company-service/companies")
	Optional<CompanyDetailsSearchResponseDto> getCompanyByManagerId(
		@RequestParam Long id
	);
}
