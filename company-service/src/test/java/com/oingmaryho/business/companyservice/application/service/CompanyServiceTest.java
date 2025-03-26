package com.oingmaryho.business.companyservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.companyservice.application.dto.mapper.CompanyApplicationMapper;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyCreateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyUpdateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.service.feignClient.HubClient;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanyType;
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;
import com.oingmaryho.business.companyservice.domain.repository.CustomCompanyRepository;
import com.oingmaryho.business.companyservice.exception.CompanyException;
import com.oingmaryho.business.companyservice.exception.ErrorCode;
import com.oingmaryho.business.companyservice.presentation.dto.response.HubSearchResponseDto;

import jakarta.persistence.EntityNotFoundException;
import jdk.jfr.Description;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
	@Mock
	private CompanyRepository companyRepository;

	@Mock
	private CustomCompanyRepository customCompanyRepository;

	@Mock
	private CompanyApplicationMapper companyApplicationMapper;

	@Mock
	private HubClient hubClient;

	@InjectMocks
	private CompanyService companyService;

	private Company company;
	private static final UUID FIXED_COMPANY_ID = UUID.fromString("1ebee6c4-5bd8-40c5-a6b4-0b0e8764a269");
	private static final UUID FIXED_MANAGE_HUB_ID = UUID.fromString("b2341dfa-2e1e-4cb2-a386-6e2d67f02d83");
	private UUID companyId;

	@BeforeEach
	void setUp() {
		companyId = FIXED_COMPANY_ID;
		company = Company.builder()
			.id(FIXED_COMPANY_ID)
			.name("리팩토링업체")
			.type(CompanyType.SUPPLIER)
			.managerId(7L)
			.manageHubId(FIXED_MANAGE_HUB_ID)
			.address("경기도 고양시 덕양구 355-11")
			.isDeleted(false)
			.build();
	}

	@Description("업체 생성 테스트 - 성공")
	@Test
	void createCompany() {
		// given
		CompanyCreateRequestServiceDto request = new CompanyCreateRequestServiceDto(
			"리팩토링업체", CompanyType.SUPPLIER, 7L, "경기도 고양시 덕양구 355-11"
		);

		when(hubClient.isManagerOfHub(7L))
			.thenReturn(Optional.of(new HubSearchResponseDto(FIXED_MANAGE_HUB_ID, "경기 북부 센터","경기도 고양시 덕양구 권율대로 570",37.6403771,126.8737955,7L)));

		when(companyRepository.existsByTypeAndAddressAndIsDeletedFalse(request.type(), request.address()))
			.thenReturn(false);
		when(companyApplicationMapper.toCreateEntity(request, FIXED_MANAGE_HUB_ID))
			.thenReturn(company);
		when(companyRepository.save(company)).thenReturn(company);

		// when
		CompanyCreateResponseServiceDto response = companyService.createCompany(request, 7L);

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(FIXED_COMPANY_ID);
	}

	@Description("업체 생성 테스트 - 실패(중복된 업체 생성)")
	@Test
	void createDuplicateCompany_409() {
		// given
		CompanyCreateRequestServiceDto request = new CompanyCreateRequestServiceDto(
			"리팩토링업체", CompanyType.SUPPLIER, 7L, "경기도 고양시 덕양구 355-11"
		);

		when(hubClient.isManagerOfHub(7L))
			.thenReturn(Optional.of(new HubSearchResponseDto(FIXED_MANAGE_HUB_ID, "경기 북부 센터", "경기도 고양시 덕양구 권율대로 570", 37.6403771, 126.8737955, 7L)));

		when(companyRepository.existsByTypeAndAddressAndIsDeletedFalse(request.type(), request.address()))
			.thenReturn(true);

		// when & then
		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyService.createCompany(request, 7L);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_REGISTERED_COMPANY);

		verify(companyRepository, never()).save(any());
	}


	@Description("업체 생성 테스트 - 마스터 존재하지 않는 허브(매니저)")
	@Test
	void createCompany_404() {
		// given
		CompanyCreateRequestServiceDto request = new CompanyCreateRequestServiceDto(
			"리팩토링업체", CompanyType.SUPPLIER, 6L, "경기도 고양시 덕양구 355-11"
		);

		when(hubClient.isManagerOfHub(6L))
			.thenReturn(Optional.empty());

		// when & then
		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyService.createCompany(request, 6L);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HUB_NOT_FOUND);

		verify(companyRepository, never()).save(any());
	}

	// @Description("업체 조회 테스트 - ID로 검색")
	// @Test
	// void getCompanyById() {
	// 	// Given
	// 	CompanyDetailsSearchRequestServiceDto requestDto = new CompanyDetailsSearchRequestServiceDto(companyId);
	//
	// 	// ✅ 올바른 개수의 인자로 객체 생성
	// 	CompanyDetailsSearchResponseServiceDto expectedResponse = new CompanyDetailsSearchResponseServiceDto(
	// 		companyId, "Test Company", "Retail",1L, FIXED_MANAGE_HUB_ID, "123 Test Street"
	// 	);
	//
	// 	when(companyRepository.findByIdAndIsDeletedFalse(companyId)).thenReturn(Optional.of(company));
	// 	when(companyApplicationMapper.toResponseDto(company)).thenReturn(expectedResponse);
	//
	// 	// When
	// 	CompanyDetailsSearchResponseServiceDto response = companyService.getCompanyById(requestDto);
	//
	// 	// Then
	// 	System.out.println("업체 정보: " + response.name());
	// 	assertThat(response).isNotNull();
	// 	assertThat(response.name()).isEqualTo("Test Company");
	// }
	//
	// @Description("업체 검색 테스트 - 페이지네이션 포함")
	// @Test
	// void searchCompanies() {
	// 	Pageable pageable = mock(Pageable.class);
	// 	CompanySearchRequestServiceDto requestDto = mock(CompanySearchRequestServiceDto.class);
	// 	Page<Company> companyPage = new PageImpl<>(List.of(company));
	// 	when(customCompanyRepository.findDynamicQuery(any(), eq(pageable))).thenReturn(companyPage);
	// 	when(companyApplicationMapper.toCompanySearchResponseServiceDto(any())).thenReturn(mock(
	// 		CompanySearchResponseServiceDto.class));
	//
	// 	Page<CompanySearchResponseServiceDto> response = companyService.searchCompanies(requestDto, pageable);
	//
	// 	assertThat(response).isNotNull();
	// 	assertThat(response.getTotalElements()).isEqualTo(1);
	// }
	//
	// @Test
	// @Transactional
	// @Description("업체 수정 테스트")
	// void updateCompany_DirtyChecking() {
	// 	CompanyUpdateRequestServiceDto requestDto = new CompanyUpdateRequestServiceDto(
	// 		companyId, "Updated Company Name", CompanyType.SUPPLIER, 2L, FIXED_MANAGE_HUB_ID, "456 New Address"
	// 	);
	//
	// 	when(companyRepository.findByIdAndIsDeletedFalse(companyId)).thenReturn(Optional.of(company));
	// 	when(companyApplicationMapper.toUpdateResponseDto(any(UUID.class)))
	// 		.thenReturn(new CompanyUpdateResponseServiceDto(FIXED_COMPANY_ID));
	//
	// 	CompanyUpdateResponseServiceDto response = companyService.updateCompany(2L, UserRoleType.HUB_MANAGER,requestDto);
	//
	// 	assertThat(company.getName()).isEqualTo("Updated Company Name");
	// 	assertThat(company.getAddress()).isEqualTo("456 New Address");
	// 	assertThat(response).isNotNull();
	// 	assertThat(response.id()).isEqualTo(companyId);
	// }


}