package com.oingmaryho.business.delivery_service.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("/company-service/companies/{id}")
    ResponseEntity<CompanyDetailsSearchResponseDto> getCompanyId(
            @PathVariable UUID id);

    @GetMapping("/company-service/companies/{id}")
    ResponseEntity<CompanyDetailsSearchResponseDto> getCompanyByManagerId(
            @RequestParam("managerId") Long managerId);
}