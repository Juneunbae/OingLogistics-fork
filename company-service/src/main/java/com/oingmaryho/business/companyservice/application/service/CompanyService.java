package com.oingmaryho.business.companyservice.application.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.companyservice.application.dto.mapper.CompanyApplicationMapper;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDeleteRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyCreateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyUpdateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.service.feignClient.HubClient;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;
import com.oingmaryho.business.companyservice.domain.repository.CustomCompanyRepository;
import com.oingmaryho.business.companyservice.exception.CompanyException;
import com.oingmaryho.business.companyservice.exception.ErrorCode;
import com.oingmaryho.business.companyservice.presentation.dto.response.HubSearchResponseDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	private final HubClient hubClient;
	private final CompanyRepository companyRepository;
	private final CustomCompanyRepository companyCustomRepository;
	private final CompanyApplicationMapper companyApplicationMapper;

	@Transactional
	public CompanyCreateResponseServiceDto createCompany(
		CompanyCreateRequestServiceDto companyCreateRequestServiceDto,
		Long requesterId) {

		Optional<HubSearchResponseDto> optionalHub = hubClient.isManagerOfHub(requesterId);
		if (optionalHub.isEmpty() || !optionalHub.get().id().equals(companyCreateRequestServiceDto.manageHubId())) {
			throw new CompanyException(ErrorCode.NO_PERMISSION);
		}

		String productCode = companyCreateRequestServiceDto.type();
		if (companyRepository.existsByProductCode(productCode)) {
			throw new CompanyException(ErrorCode.ALREADY_REGISTERED_PRODUCT_CODE);
		}

		Company company = companyApplicationMapper.toCreateEntity(companyCreateRequestServiceDto);
		Company savedCompany = companyRepository.save(company);
		return new CompanyCreateResponseServiceDto(savedCompany.getId());
	}

	public CompanyDetailsSearchResponseServiceDto getCompanyById(CompanyDetailsSearchRequestServiceDto requestDto) {
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new CompanyException(ErrorCode.NOT_FOUND));
		return companyApplicationMapper.toResponseDto(company);
	}

	public Page<CompanySearchResponseServiceDto> searchCompanies(CompanySearchRequestServiceDto requestDto, Pageable pageable) {
		Page<Company> companies = companyCustomRepository.findDynamicQuery(createCompanySearchCriteria(requestDto), pageable);

		return companies.map(companyApplicationMapper::toCompanySearchResponseServiceDto);
	}

	@Transactional
	public CompanyUpdateResponseServiceDto updateCompany(CompanyUpdateRequestServiceDto requestServiceDto) {
		// TODO: 권한 체크(수정 권한은 허브 담당자와 업체 담당자)
		// TODO: 공통 예외처리 코드 작성 필요
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new CompanyException(ErrorCode.NOT_FOUND));

		company.update(
			requestServiceDto.name(),
			requestServiceDto.type(),
			requestServiceDto.manageHubId(),
			requestServiceDto.address()
		);

		return companyApplicationMapper.toUpdateResponseDto(company.getId());
	}

	@Transactional
	public void deleteCompany(Long userId, CompanyDeleteRequestServiceDto requestServiceDto) {
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new CompanyException(ErrorCode.NOT_FOUND));
		company.softDelete(userId);
	}

	private CompanySearchCriteria createCompanySearchCriteria(CompanySearchRequestServiceDto requestDto){
		return CompanySearchCriteria.builder()
			.id(requestDto.id())
			.name(requestDto.name())
			.type(requestDto.type())
			.managerId(requestDto.managerId())
			.manageHubId(requestDto.manageHubId())
			.address(requestDto.address())
			.isDeleted(Boolean.FALSE)
			.build();

	}


}
