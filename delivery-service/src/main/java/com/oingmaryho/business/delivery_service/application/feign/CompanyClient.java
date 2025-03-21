package com.oingmaryho.business.delivery_service.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("/company-service/companies/{id}")
    CompanyDetailsSearchResponseDto getCompany(@PathVariable UUID id);
}