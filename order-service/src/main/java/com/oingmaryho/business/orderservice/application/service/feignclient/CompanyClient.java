package com.oingmaryho.business.orderservice.application.service.feignclient;

import com.oingmaryho.business.orderservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("/company-service/companies/{id}")
    Optional<CompanyDetailsSearchResponseDto> getCompany(@PathVariable UUID id);
}