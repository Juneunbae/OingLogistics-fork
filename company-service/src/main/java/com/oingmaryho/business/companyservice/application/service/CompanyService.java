package com.oingmaryho.business.companyservice.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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
import com.oingmaryho.business.companyservice.application.event.CompanyProductDeletePublisher;
import com.oingmaryho.business.companyservice.application.service.feignClient.HubClient;
import com.oingmaryho.business.companyservice.config.cache.CacheType;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;
import com.oingmaryho.business.companyservice.domain.CompanyType;
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;
import com.oingmaryho.business.companyservice.domain.repository.CustomCompanyRepository;
import com.oingmaryho.business.companyservice.exception.CompanyException;
import com.oingmaryho.business.companyservice.exception.ErrorCode;
import com.oingmaryho.business.companyservice.presentation.dto.response.HubSearchResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	@Value("${rabbitmq.queue.company-deleted}")
	private String queueCompanyDelete;

	private final HubClient hubClient;
	private final RabbitTemplate rabbitTemplate;
	private final CompanyRepository companyRepository;
	private final CustomCompanyRepository companyCustomRepository;
	private final CompanyApplicationMapper companyApplicationMapper;
	private final CompanyProductDeletePublisher companyProductDeletePublisher;

	@Transactional
	public CompanyCreateResponseServiceDto createCompany(
		CompanyCreateRequestServiceDto companyCreateRequestServiceDto,
		Long requesterId) {

		HubSearchResponseDto hubSearchResponseDto = validateManageHubPermission(requesterId);

		// 업체가 중복되는지 확인하기 위한 코드로, company entity 에서 jpa 조회(기준은 type, address 로 변경)
		String address = companyCreateRequestServiceDto.address();
		CompanyType type = companyCreateRequestServiceDto.type();
		if (companyRepository.existsByTypeAndAddressAndIsDeletedFalse(type, address)) {
			throw new CompanyException(ErrorCode.ALREADY_REGISTERED_COMPANY);
		}

		Company company = companyApplicationMapper.toCreateEntity(companyCreateRequestServiceDto, hubSearchResponseDto.id());
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
	public CompanyUpdateResponseServiceDto updateCompany(Long requesterId, UserRoleType role,CompanyUpdateRequestServiceDto requestServiceDto) {
		Company company = companyRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new CompanyException(ErrorCode.NOT_FOUND));
		validateUpdatePermission(requesterId,role, company);
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

		HubSearchResponseDto hubSearchResponseDto = validateManageHubPermission(requesterId);

		if (!hubSearchResponseDto.id().equals(company.getManageHubId())) {
			throw new CompanyException(ErrorCode.NO_PERMISSION);
		}

		company.softDelete(requesterId);

		CompanyProductDeleteRequestDto message = new CompanyProductDeleteRequestDto(company.getId(), requesterId);
		companyProductDeletePublisher.publish(message);
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

	private HubSearchResponseDto validateManageHubPermission(Long requesterId) {

		return hubClient.isManagerOfHub(requesterId)
			.orElseThrow(() -> new CompanyException(ErrorCode.HUB_NOT_FOUND));

	}
	private void validateUpdatePermission(Long requesterId, UserRoleType role, Company company) {
		switch (role) {
			case HUB_MANAGER -> {
				Optional<HubSearchResponseDto> optionalHub = hubClient.isManagerOfHub(requesterId);
				if (optionalHub.isPresent() && optionalHub.get().id().equals(company.getManageHubId())) {
					return;
				}
				throw new CompanyException(ErrorCode.NO_PERMISSION);
			}
			case COMPANY_MANAGER -> {
				if (company.getManagerId().equals(requesterId)) {
					return;
				}
				throw new CompanyException(ErrorCode.NO_PERMISSION);
			}
			default -> throw new CompanyException(ErrorCode.NO_PERMISSION);
		}
	}


}
