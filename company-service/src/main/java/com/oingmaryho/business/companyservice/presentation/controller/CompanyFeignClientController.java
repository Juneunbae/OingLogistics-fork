package com.oingmaryho.business.companyservice.presentation.controller;

import java.util.UUID;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.service.CompanyFeignService;
import com.oingmaryho.business.companyservice.presentation.dto.mapper.CompanyPresentationMapper;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyDetailsSearchResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/company-service/companies")
@RequiredArgsConstructor
public class CompanyFeignClientController {

	private final CompanyFeignService companyFeignService;
	private final CompanyPresentationMapper companyPresentationMapper;

	@Description("FeignClient - 업체 상세 조회")
	@GetMapping("/{id}")
	public ResponseEntity<CompanyDetailsSearchResponseDto> getCompanyById(@PathVariable UUID id) {
		CompanyDetailsSearchRequestServiceDto requestServiceDto = companyPresentationMapper.toDetailsSearchServiceDto(id);
		CompanyDetailsSearchResponseServiceDto responseServiceDto = companyFeignService.getCompanyById(requestServiceDto);
		CompanyDetailsSearchResponseDto responseDto = companyPresentationMapper.toDetailsSearchResponseDto(responseServiceDto);
		return ResponseEntity.ok(responseDto);
	}
}
