package com.oingmaryho.business.companyservice.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.companyservice.application.dto.mapper.CompanyApplicationMapper;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDeleteRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyProductDeleteRequestDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyCreateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyUpdateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.service.feignClient.HubClient;
import com.oingmaryho.business.companyservice.config.cache.CacheType;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;
import com.oingmaryho.business.companyservice.domain.repository.CustomCompanyRepository;
import com.oingmaryho.business.companyservice.exception.CompanyException;
import com.oingmaryho.business.companyservice.exception.ErrorCode;
import com.oingmaryho.business.companyservice.presentation.dto.response.HubSearchResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyAdminService {
	private final HubClient hubClient;
	private final RabbitTemplate rabbitTemplate;
	private final CompanyRepository companyRepository;
	private final CustomCompanyRepository companyCustomRepository;
	private final CompanyApplicationMapper companyApplicationMapper;

	@Transactional
	public CompanyCreateResponseServiceDto createCompany(
		CompanyCreateRequestServiceDto companyCreateRequestServiceDto
	) {

		String address = companyCreateRequestServiceDto.address();
		if (companyRepository.existsByAddressAndIsDeletedFalse(address)) {
			throw new CompanyException(ErrorCode.ALREADY_REGISTERED_COMPANY);
		}
		HubSearchResponseDto hub = hubClient(companyCreateRequestServiceDto.manageHubId());
		Company company = companyApplicationMapper.toCreateEntity(companyCreateRequestServiceDto, hub.id());
		Company savedCompany = companyRepository.save(company);
		return new CompanyCreateResponseServiceDto(savedCompany.getId());
	}

	@Cacheable(value = CacheType.COMPANY_CACHE, key = "#requestDto.id")
	public CompanyDetailsSearchResponseServiceDto getCompanyById(CompanyDetailsSearchRequestServiceDto requestDto) {
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new CompanyException(ErrorCode.NOT_FOUND));
		return companyApplicationMapper.toResponseDto(company);
	}

	public Page<CompanySearchResponseServiceDto> searchCompanies(CompanySearchRequestServiceDto requestDto, Pageable pageable) {
		Page<Company> companies = companyCustomRepository.findDynamicQuery(createCompanySearchCriteria(requestDto), pageable);

		return companies.map(companyApplicationMapper::toCompanySearchResponseServiceDto);
	}

	@CacheEvict(value = CacheType.COMPANY_CACHE, key = "#requestServiceDto.id")
	@Transactional
	public CompanyUpdateResponseServiceDto updateCompany(CompanyUpdateRequestServiceDto requestServiceDto) {
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

	@CacheEvict(value = CacheType.COMPANY_CACHE, key = "#requestServiceDto.id")
	@Transactional
	public void deleteCompany(Long requesterId, CompanyDeleteRequestServiceDto requestServiceDto) {
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new CompanyException(ErrorCode.NOT_FOUND));

		company.softDelete(requesterId);
		CompanyProductDeleteRequestDto message = new CompanyProductDeleteRequestDto(company.getId(), requesterId);

		rabbitTemplate.convertAndSend(
			"company.exchange",
			"company.deleted",
			message
		);
	}

	private CompanySearchCriteria createCompanySearchCriteria(CompanySearchRequestServiceDto requestDto){
		return CompanySearchCriteria.builder()
			.id(requestDto.id())
			.name(requestDto.name())
			.type(requestDto.type())
			.managerId(requestDto.managerId())
			.manageHubId(requestDto.manageHubId())
			.address(requestDto.address())
			.isDeleted(requestDto.isDeleted())
			.build();

	}

	private HubSearchResponseDto hubClient(UUID manageHubId) {
		return hubClient.getHubById(manageHubId)
			.orElseThrow(() -> new CompanyException(ErrorCode.HUB_NOT_FOUND));
	}

}
