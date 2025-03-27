package com.oingmaryho.business.companyservice.presentation.controller;

import java.util.UUID;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.service.CompanyFeignService;
import com.oingmaryho.business.companyservice.presentation.dto.mapper.CompanyPresentationMapper;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanySearchRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanySearchResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/company-service/companies")
@RequiredArgsConstructor
public class CompanyFeignClientController {

	private final CompanyFeignService companyFeignService;
	private final CompanyPresentationMapper companyPresentationMapper;

	@Description("FeignClient - 업체 상세 조회")
	@GetMapping("/{id}")
	public ResponseEntity<CompanyDetailsSearchResponseDto> getCompany(@PathVariable UUID id) {
		CompanyDetailsSearchRequestServiceDto requestServiceDto = companyPresentationMapper.toDetailsSearchServiceDto(id);
		CompanyDetailsSearchResponseServiceDto responseServiceDto = companyFeignService.getCompany(requestServiceDto);
		CompanyDetailsSearchResponseDto responseDto = companyPresentationMapper.toDetailsSearchResponseDto(responseServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("FeignClient - 업체 담당자 기반 업체 상세 조회")
	@GetMapping
	public ResponseEntity<CompanyDetailsSearchResponseDto> getCompanyByManagerId(
		@RequestParam Long id
	){
		CompanySearchRequestDto requestDto =
			new CompanySearchRequestDto(null, null, null, id, null, null, null);
		CompanyDetailsSearchResponseServiceDto responseServiceDto = companyFeignService.getCompanyByManagerId(companyPresentationMapper.toCompanySearchResponseServiceDto(requestDto));
		CompanyDetailsSearchResponseDto responseDto = companyPresentationMapper.toDetailsSearchResponseDto(responseServiceDto);
		return ResponseEntity.ok(responseDto);
	}
}
