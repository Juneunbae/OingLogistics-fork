package com.oingmaryho.business.companyservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;

import jakarta.persistence.EntityNotFoundException;
import jdk.jfr.Description;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
	@Mock
	private CompanyRepository companyRepository;

	@Mock
	private CompanyApplicationMapper companyApplicationMapper;

	@InjectMocks
	private CompanyService companyService;

	private Company company;
	private static final UUID FIXED_COMPANY_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	private UUID companyId;

	@BeforeEach
	void setUp() {
		companyId = FIXED_COMPANY_ID;
		company = Company.builder()
			.id(FIXED_COMPANY_ID)
			.name("Test Company")
			.type("Retail")
			.manageHubId(FIXED_COMPANY_ID)
			.address("123 Test Street")
			.isDeleted(false)
			.build();
	}

	@Test
	@Description("업체 생성 테스트")
	void createCompany() {
		// Given
		CompanyCreateRequestServiceDto requestDto = new CompanyCreateRequestServiceDto(
			"Test Company",
			"Retail",
			FIXED_COMPANY_ID,
			"123 Test Street"
		);

		CompanyCreateResponseServiceDto expectedResponse = new CompanyCreateResponseServiceDto(FIXED_COMPANY_ID);

		when(companyApplicationMapper.toCreateEntity(any(CompanyCreateRequestServiceDto.class))).thenReturn(company);
		when(companyRepository.save(any(Company.class))).thenReturn(company);

		// When
		CompanyCreateResponseServiceDto response = companyService.createCompany(requestDto);
		System.out.println("생성된 업체 정보: " + response.id());

		// Then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(expectedResponse.id());
	}

	@Description("업체 조회 테스트 - ID로 검색")
	@Test
	void getCompanyById() {
		// Given
		CompanyDetailsSearchRequestServiceDto requestDto = new CompanyDetailsSearchRequestServiceDto(companyId);

		// ✅ 올바른 개수의 인자로 객체 생성
		CompanyDetailsSearchResponseServiceDto expectedResponse = new CompanyDetailsSearchResponseServiceDto(
			companyId, "Test Company", "Retail", FIXED_COMPANY_ID, "123 Test Street"
		);

		when(companyRepository.findByIdAndIsDeletedFalse(companyId)).thenReturn(Optional.of(company));
		when(companyApplicationMapper.toResponseDto(company)).thenReturn(expectedResponse);

		// When
		CompanyDetailsSearchResponseServiceDto response = companyService.getCompanyById(requestDto);

		// Then
		System.out.println("업체 정보: " + response.name());
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("Test Company");
	}



	@Description("업체 검색 테스트 - 페이지네이션 포함")
	@Test
	void searchCompanies() {
		Pageable pageable = mock(Pageable.class);
		CompanySearchRequestServiceDto requestDto = mock(CompanySearchRequestServiceDto.class);
		Page<Company> companyPage = new PageImpl<>(List.of(company));
		when(companyRepository.findDynamicQuery(any(), eq(pageable))).thenReturn(companyPage);
		when(companyApplicationMapper.toCompanySearchResponseServiceDto(any())).thenReturn(mock(
			CompanySearchResponseServiceDto.class));

		Page<CompanySearchResponseServiceDto> response = companyService.searchCompanies(requestDto, pageable);

		assertThat(response).isNotNull();
		assertThat(response.getTotalElements()).isEqualTo(1);
	}

	@Test
	@Transactional
	@Description("업체 수정 테스트")
	void updateCompany_DirtyChecking() {
		CompanyUpdateRequestServiceDto requestDto = new CompanyUpdateRequestServiceDto(
			companyId, "Updated Company Name", "Retail", FIXED_COMPANY_ID, "456 New Address"
		);

		when(companyRepository.findByIdAndIsDeletedFalse(companyId)).thenReturn(Optional.of(company));
		when(companyApplicationMapper.toUpdateResponseDto(any(UUID.class)))
			.thenReturn(new CompanyUpdateResponseServiceDto(FIXED_COMPANY_ID));

		CompanyUpdateResponseServiceDto response = companyService.updateCompany(requestDto);

		assertThat(company.getName()).isEqualTo("Updated Company Name");
		assertThat(company.getAddress()).isEqualTo("456 New Address");
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(companyId);
	}


}