package com.oingmaryho.business.companyservice.presentation.controller;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDeleteRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.service.CompanyService;
import com.oingmaryho.business.companyservice.config.pageable.PageableConfig;
import com.oingmaryho.business.companyservice.presentation.dto.mapper.CompanyPresentationMapper;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanyCreateRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanySearchRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanyUpdateRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyCreateResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyDeleteResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanySearchResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyUpdateResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
public class CompanyController {
	private final PageableConfig pageableConfig;
	private final CompanyService companyService;
	private final CompanyPresentationMapper companyPresentationMapper;

	@Description("일반 - 업체 생성")
	@PostMapping("/api/v1/companies")
	public ResponseEntity<CompanyCreateResponseDto> createCompany(@RequestBody CompanyCreateRequestDto companyCreateRequestDto) {
		CompanyCreateRequestServiceDto requestServiceDto = companyPresentationMapper.toCreateServiceDto(companyCreateRequestDto);
		// TODO : Service Method 활용 및 presentation dto 로 변환
		return null;
	}

	@Description("일반 - 업체 조회")
	@GetMapping("/api/v1/companies")
	public ResponseEntity<CompanySearchResponseDto> getCompany(
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sortDirection", required = false) String sortDirection,
		@RequestBody CompanySearchRequestDto companySearchRequestDto
	) {
		Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
		CompanySearchRequestServiceDto requestServiceDto = companyPresentationMapper.toSearchServiceDto(companySearchRequestDto,customPageable);
		// TODO : Service Method 활용 및 presentation dto 로 변환
		return null;
	}

	@Description("일반 - 업체 상세 조회")
	@GetMapping("/{id}")
	public ResponseEntity<CompanyDetailsSearchResponseDto> getCompanyById(@PathVariable UUID id) {
		CompanyDetailsSearchRequestServiceDto requestServiceDto = companyPresentationMapper.toDetailsSearchServiceDto(id);
		CompanyDetailsSearchResponseServiceDto responseServiceDto = companyService.getCompanyById(requestServiceDto);
		CompanyDetailsSearchResponseDto responseDto = companyPresentationMapper.toDetailsSearchResponseDto(responseServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("일반 - 업체 수정")
	@PutMapping("/api/v1/companies/{id}")
	public ResponseEntity<CompanyUpdateResponseDto> updateCompany(@PathVariable UUID id, @RequestBody CompanyUpdateRequestDto companyUpdateRequestDto) {
		CompanyUpdateRequestServiceDto requestServiceDto = companyPresentationMapper.toUpdateServiceDto(id,companyUpdateRequestDto);
		// TODO : Service Method 활용 및 presentation dto 로 변환
		return null;
	}

	@Description("일반 - 업체 식제")
	@DeleteMapping("/api/v1/companies/{id}")
	public ResponseEntity<CompanyDeleteResponseDto> deleteCompany(@PathVariable UUID id) {
		CompanyDeleteRequestServiceDto requestServiceDto = companyPresentationMapper.toDeleteServiceDto(id);
		// TODO : Service Method 활용 및 presentation dto 로 변환
		return null;
	}

}
