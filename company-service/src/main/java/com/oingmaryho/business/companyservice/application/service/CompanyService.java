package com.oingmaryho.business.companyservice.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.oingmaryho.business.companyservice.application.dto.mapper.CompanyApplicationMapper;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	private final CompanyRepository companyRepository;
	private final CompanyApplicationMapper companyApplicationMapper;

	public CompanyDetailsSearchResponseServiceDto getCompanyById(CompanyDetailsSearchRequestServiceDto requestDto) {
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다: " + requestDto.id()));
		return companyApplicationMapper.toResponseDto(company);
	}

	public Page<CompanySearchResponseServiceDto> searchCompanies(CompanySearchRequestServiceDto requestDto, Pageable pageable) {
		Page<Company> companies = companyRepository.findDynamicQuery(createCompanySearchCriteria(requestDto), pageable);

		return companies.map(companyApplicationMapper::toCompanySearchResponseServiceDto);
	}

	private CompanySearchCriteria createCompanySearchCriteria(CompanySearchRequestServiceDto requestDto){
		return CompanySearchCriteria.builder()
			.id(requestDto.id())
			.name(requestDto.name())
			.type(requestDto.type())
			.managerId(requestDto.managerId())
			.manageHubId(requestDto.manageHubId())
			.address(requestDto.address())
			.build();

	}
}
