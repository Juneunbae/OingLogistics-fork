package com.oingmaryho.business.companyservice.application.service;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.companyservice.application.dto.mapper.CompanyApplicationMapper;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyDetailsSearchResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyFeignService {
	private final CompanyRepository companyRepository;
	private final CompanyApplicationMapper companyApplicationMapper;

	public CompanyDetailsSearchResponseServiceDto getCompanyById(CompanyDetailsSearchRequestServiceDto requestDto) {
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다: " + requestDto.id()));
		return companyApplicationMapper.toResponseDto(company);
	}
}
