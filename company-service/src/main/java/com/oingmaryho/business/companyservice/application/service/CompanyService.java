package com.oingmaryho.business.companyservice.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.companyservice.application.dto.mapper.CompanyApplicationMapper;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyCreateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyUpdateResponseServiceDto;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	private final CompanyRepository companyRepository;
	private final CompanyApplicationMapper companyApplicationMapper;

	@Transactional
	public CompanyCreateResponseServiceDto createCompany(CompanyCreateRequestServiceDto companyCreateRequestServiceDto) {
		// TODO : role 체크 및 권한에 따른 비지니스 로직 분리(마스터, 허브 관리자는 담당 허브만 생성하게끔)
		Company company = companyApplicationMapper.toCreateEntity(companyCreateRequestServiceDto);
		Company savedCompany = companyRepository.save(company);
		return new CompanyCreateResponseServiceDto(savedCompany.getId());
	}

	public CompanyDetailsSearchResponseServiceDto getCompanyById(CompanyDetailsSearchRequestServiceDto requestDto) {
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다: " + requestDto.id()));
		return companyApplicationMapper.toResponseDto(company);
	}

	public Page<CompanySearchResponseServiceDto> searchCompanies(CompanySearchRequestServiceDto requestDto, Pageable pageable) {
		Page<Company> companies = companyRepository.findDynamicQuery(createCompanySearchCriteria(requestDto), pageable);

		return companies.map(companyApplicationMapper::toCompanySearchResponseServiceDto);
	}

	@Transactional
	public CompanyUpdateResponseServiceDto updateCompany(CompanyUpdateRequestServiceDto requestServiceDto) {
		// TODO: 권한 체크(수정 권한은 허브 담당자와 업체 담당자)
		// TODO: 공통 예외처리 코드 작성 필요
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new EntityNotFoundException("업체를 찾을 수 없습니다: " + requestServiceDto.id()));
		Company updatedCompany = companyRepository.update(company);

		return companyApplicationMapper.toUpdateResponseDto(updatedCompany);
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
