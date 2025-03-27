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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.companyservice.application.dto.mapper.CompanyApplicationMapper;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyAdminCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDeleteRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyCreateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyUpdateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.event.CompanyProductDeletePublisher;
import com.oingmaryho.business.companyservice.application.service.feignClient.HubClient;
import com.oingmaryho.business.companyservice.application.service.feignClient.UserClient;
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

	@Mock
	private UserClient userClient;

	@Mock
	private CompanyProductDeletePublisher companyProductDeletePublisher;

	@InjectMocks
	private CompanyService companyService;

	@InjectMocks
	private CompanyAdminService companyAdminService;

	private Company company;
	private UUID companyId;

	private static final UUID FIXED_COMPANY_ID = UUID.fromString("1ebee6c4-5bd8-40c5-a6b4-0b0e8764a269");
	private static final UUID FIXED_MANAGE_HUB_ID = UUID.fromString("b2341dfa-2e1e-4cb2-a386-6e2d67f02d83");

	private static final Long VALID_REQUESTER_ID= 7L;
	private static final Long VALID_COMPANY_MANAGER_ID = 5L;
	private static final Long VALID_MANAGER_ID = 7L;
	private static final Long INVALID_MANAGER_ID = 6L;

	private static final String COMPANY_NAME = "리팩토링업체";
	private static final String UPDATE_COMPANY_NAME = "변경된 리팩토링업체";
	private static final CompanyType COMPANY_TYPE = CompanyType.SUPPLIER;
	private static final String COMPANY_ADDRESS = "경기도 고양시 덕양구 355-11";
	private static final String UPDATE_COMPANY_ADDRESS = "경기도 고양시 마두동";

	private static final String HUB_NAME = "경기 북부 센터";
	private static final String HUB_ADDRESS = "경기도 고양시 덕양구 권율대로 570";
	private static final double HUB_LATITUDE = 37.6403771;
	private static final double HUB_LONGITUDE = 126.8737955;

	private static final UUID OTHER_HUB_ID = UUID.fromString("39b481dc-8fac-4349-857b-76f016ac92d1");
	private static final String OTHER_HUB_NAME = "경기 남부 센터";
	private static final String OTHER_HUB_ADDRESS = "경기도 이천시 덕평로 257-21";
	private static final double OTHER_HUB_LAT = 37.1896213;
	private static final double OTHER_HUB_LNG = 127.3750501;
	private static final Long OTHER_HUB_MANAGER_ID = 8L;

	private static final Long REQUESTER_ID = 123L;

	private static final UUID DELETE_COMPANY_ID = UUID.fromString("37323633-cd91-40aa-bb13-5cfd3c03ccbc");
	private static final String DELETE_COMPANY_NAME = "리팩토링업체";
	private static final String DELETE_COMPANY_ADDRESS = "경기도 고양시 덕양구 355-11";
	private static final UUID HUB_ID_MATCH = UUID.fromString("39b481dc-8fac-4349-857b-76f016ac92d1");
	private static final UUID HUB_ID_MISMATCH = UUID.fromString("b9999999-8888-7777-6666-555555555555");


	@BeforeEach
	void setUp() {
		companyId = FIXED_COMPANY_ID;
		company = Company.builder()
			.id(FIXED_COMPANY_ID)
			.name(COMPANY_NAME)
			.type(COMPANY_TYPE)
			.managerId(VALID_MANAGER_ID)
			.manageHubId(FIXED_MANAGE_HUB_ID)
			.address(COMPANY_ADDRESS)
			.isDeleted(false)
			.build();
	}

	@Description("업체 생성 테스트 - 성공")
	@Test
	void createCompany() {
		// given
		CompanyCreateRequestServiceDto request = new CompanyCreateRequestServiceDto(
			COMPANY_NAME, COMPANY_TYPE, VALID_COMPANY_MANAGER_ID, COMPANY_ADDRESS
		);

		when(hubClient.isManagerOfHub(VALID_REQUESTER_ID))
			.thenReturn(Optional.of(new HubSearchResponseDto(FIXED_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, VALID_REQUESTER_ID)));

		when(userClient.userFeignServiceGetRoleById(VALID_COMPANY_MANAGER_ID))
			.thenReturn(Optional.of(UserRoleType.COMPANY_MANAGER));

		when(companyRepository.existsByTypeAndAddressAndIsDeletedFalse(COMPANY_TYPE, COMPANY_ADDRESS))
			.thenReturn(false);
		when(companyApplicationMapper.toCreateEntity(request, FIXED_MANAGE_HUB_ID))
			.thenReturn(company);
		when(companyRepository.save(company)).thenReturn(company);

		// when
		CompanyCreateResponseServiceDto response = companyService.createCompany(request, VALID_REQUESTER_ID);

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(FIXED_COMPANY_ID);
	}

	@Description("업체 생성 테스트 - 실패(중복된 업체 생성)")
	@Test
	void createDuplicateCompany_409() {
		// given
		CompanyCreateRequestServiceDto request = new CompanyCreateRequestServiceDto(
			COMPANY_NAME, COMPANY_TYPE, VALID_COMPANY_MANAGER_ID, COMPANY_ADDRESS
		);

		when(hubClient.isManagerOfHub(VALID_REQUESTER_ID))
			.thenReturn(Optional.of(new HubSearchResponseDto(FIXED_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, VALID_REQUESTER_ID)));

		when(userClient.userFeignServiceGetRoleById(VALID_COMPANY_MANAGER_ID))
			.thenReturn(Optional.of(UserRoleType.COMPANY_MANAGER));

		when(companyRepository.existsByTypeAndAddressAndIsDeletedFalse(COMPANY_TYPE, COMPANY_ADDRESS))
			.thenReturn(true);

		// when & then
		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyService.createCompany(request, VALID_REQUESTER_ID);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_REGISTERED_COMPANY);

		verify(companyRepository, never()).save(any());
	}


	@Description("업체 생성 테스트 - 마스터 존재하지 않는 허브")
	@Test
	void createCompany_404() {
		// given
		CompanyAdminCreateRequestServiceDto request = new CompanyAdminCreateRequestServiceDto(
			COMPANY_NAME, COMPANY_TYPE, VALID_COMPANY_MANAGER_ID, HUB_ID_MISMATCH, COMPANY_ADDRESS
		);

		when(hubClient.getHubById(HUB_ID_MISMATCH))
			.thenReturn(Optional.empty());

		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyAdminService.createCompany(request);
		});
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HUB_NOT_FOUND);

		verify(companyRepository, never()).save(any());
	}

	@Description("업체 상세 조회 - 성공")
	@Test
	void getCompanyById() {
		// given
		CompanyDetailsSearchRequestServiceDto request = new CompanyDetailsSearchRequestServiceDto(FIXED_COMPANY_ID);
		CompanyDetailsSearchResponseServiceDto expectedResponse = new CompanyDetailsSearchResponseServiceDto(
			FIXED_COMPANY_ID,
			COMPANY_NAME,
			COMPANY_TYPE.name(),
			VALID_MANAGER_ID,
			FIXED_MANAGE_HUB_ID,
			COMPANY_ADDRESS
		);

		when(companyRepository.findByIdAndIsDeletedFalse(FIXED_COMPANY_ID))
			.thenReturn(Optional.of(company));
		when(companyApplicationMapper.toResponseDto(company))
			.thenReturn(expectedResponse);

		// when
		CompanyDetailsSearchResponseServiceDto result = companyService.getCompanyById(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(FIXED_COMPANY_ID);
		assertThat(result.name()).isEqualTo(COMPANY_NAME);
		assertThat(result.managerId()).isEqualTo(VALID_MANAGER_ID);
	}

	@Description("업체 상세 조회 - 실패 (존재하지 않는 ID)")
	@Test
	void getCompanyById_404() {
		// given
		UUID invalidId = UUID.randomUUID();
		CompanyDetailsSearchRequestServiceDto request = new CompanyDetailsSearchRequestServiceDto(invalidId);

		when(companyRepository.findByIdAndIsDeletedFalse(invalidId))
			.thenReturn(Optional.empty());

		// when & then
		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyService.getCompanyById(request);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
	}

	@Description("업체 검색 테스트 - 성공")
	@Test
	void searchCompanies() {
		// given
		CompanySearchRequestServiceDto requestDto = new CompanySearchRequestServiceDto(
			null, COMPANY_TYPE, COMPANY_NAME, VALID_MANAGER_ID, FIXED_MANAGE_HUB_ID, COMPANY_ADDRESS, false
		);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Company> companyPage = new PageImpl<>(List.of(company));

		when(customCompanyRepository.findDynamicQuery(any(), eq(pageable))).thenReturn(companyPage);
		when(companyApplicationMapper.toCompanySearchResponseServiceDto(any())).thenReturn(
			new CompanySearchResponseServiceDto(
				FIXED_COMPANY_ID,
				COMPANY_NAME,
				COMPANY_TYPE,
				VALID_MANAGER_ID,
				FIXED_MANAGE_HUB_ID,
				COMPANY_ADDRESS,
				false
			)
		);

		// when
		Page<CompanySearchResponseServiceDto> result = companyService.searchCompanies(requestDto, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).name()).isEqualTo(COMPANY_NAME);
	}

	@Description("업체 검색 테스트 - 성공(검색 결과 없음)")
	@Test
	void searchCompanies_emptyResult() {
		// given
		CompanySearchRequestServiceDto requestDto = new CompanySearchRequestServiceDto(
			null, CompanyType.RECEIVER, "없는 이름", null, null, null, false
		);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Company> emptyPage = new PageImpl<>(List.of());

		when(customCompanyRepository.findDynamicQuery(any(), eq(pageable))).thenReturn(emptyPage);

		// when
		Page<CompanySearchResponseServiceDto> result = companyService.searchCompanies(requestDto, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}

	@Description("업체 수정 테스트 - 성공")
	@Test
	void updateCompany() {
		// given
		CompanyUpdateRequestServiceDto requestDto = new CompanyUpdateRequestServiceDto(
			FIXED_COMPANY_ID,
			UPDATE_COMPANY_NAME,
			COMPANY_TYPE,
			VALID_MANAGER_ID,
			FIXED_MANAGE_HUB_ID,
			UPDATE_COMPANY_ADDRESS
		);

		when(companyRepository.findByIdAndIsDeletedFalse(FIXED_COMPANY_ID))
			.thenReturn(Optional.of(company));

		// 권한 체크가 void라 따로 when 필요 없음
		when(companyApplicationMapper.toUpdateResponseDto(FIXED_COMPANY_ID))
			.thenReturn(new CompanyUpdateResponseServiceDto(FIXED_COMPANY_ID));

		// when
		CompanyUpdateResponseServiceDto response = companyService.updateCompany(
			VALID_MANAGER_ID,
			UserRoleType.COMPANY_MANAGER,
			requestDto
		);

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(FIXED_COMPANY_ID);
		assertThat(company.getName()).isEqualTo(UPDATE_COMPANY_NAME);
		assertThat(company.getAddress()).isEqualTo(UPDATE_COMPANY_ADDRESS);
	}

	@Description("업체 수정 테스트 - 실패: 본인 담당 허브가 아닌 수정")
	@Test
	void updateCompany_403_hubManager_wrongHub() {
		// given
		CompanyUpdateRequestServiceDto requestDto = new CompanyUpdateRequestServiceDto(
			FIXED_COMPANY_ID,
			UPDATE_COMPANY_NAME,
			COMPANY_TYPE,
			VALID_MANAGER_ID,
			FIXED_MANAGE_HUB_ID,
			UPDATE_COMPANY_ADDRESS
		);

		when(companyRepository.findByIdAndIsDeletedFalse(FIXED_COMPANY_ID)).thenReturn(Optional.of(company));

		when(hubClient.isManagerOfHub(VALID_MANAGER_ID))
			.thenReturn(Optional.of(new HubSearchResponseDto(
				OTHER_HUB_ID,
				OTHER_HUB_NAME,
				OTHER_HUB_ADDRESS,
				OTHER_HUB_LAT,
				OTHER_HUB_LNG,
				OTHER_HUB_MANAGER_ID
			)));

		// when & then
		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyService.updateCompany(VALID_MANAGER_ID, UserRoleType.HUB_MANAGER, requestDto);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION);
	}

	@Description("업체 수정 테스트 - 실패: 본인 업체가 아닌 수정")
	@Test
	void updateCompany_403_companyManager_wrongManager() {
		// given
		CompanyUpdateRequestServiceDto requestDto = new CompanyUpdateRequestServiceDto(
			FIXED_COMPANY_ID,
			UPDATE_COMPANY_NAME,
			COMPANY_TYPE,
			OTHER_HUB_MANAGER_ID,
			FIXED_MANAGE_HUB_ID,
			UPDATE_COMPANY_ADDRESS
		);

		when(companyRepository.findByIdAndIsDeletedFalse(FIXED_COMPANY_ID)).thenReturn(Optional.of(company));

		// when & then
		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyService.updateCompany(OTHER_HUB_MANAGER_ID, UserRoleType.COMPANY_MANAGER, requestDto);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION);
	}

	@Description("업체 삭제 테스트 - 성공")
	@Test
	void deleteCompany() {
		CompanyDeleteRequestServiceDto requestDto = new CompanyDeleteRequestServiceDto(DELETE_COMPANY_ID);

		Company companyToDelete = Company.builder()
			.id(DELETE_COMPANY_ID)
			.name(DELETE_COMPANY_NAME)
			.type(CompanyType.SUPPLIER)
			.managerId(REQUESTER_ID)
			.manageHubId(HUB_ID_MATCH)
			.address(DELETE_COMPANY_ADDRESS)
			.isDeleted(false)
			.build();

		when(companyRepository.findByIdAndIsDeletedFalse(DELETE_COMPANY_ID)).thenReturn(Optional.of(companyToDelete));
		when(hubClient.isManagerOfHub(REQUESTER_ID)).thenReturn(Optional.of(new HubSearchResponseDto(HUB_ID_MATCH, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, REQUESTER_ID)));

		doNothing().when(companyProductDeletePublisher).publish(any());

		// when
		companyService.deleteCompany(REQUESTER_ID, requestDto);

		// then
		assertThat(companyToDelete.getIsDeleted()).isTrue();
		verify(companyRepository).findByIdAndIsDeletedFalse(DELETE_COMPANY_ID);
		verify(companyProductDeletePublisher).publish(any());
	}
	@Description("업체 삭제 테스트 - 실패: 담당 허브가 다른 경우")
	@Test
	void deleteCompany_403() {
		CompanyDeleteRequestServiceDto requestDto = new CompanyDeleteRequestServiceDto(DELETE_COMPANY_ID);

		Company companyToDelete = Company.builder()
			.id(DELETE_COMPANY_ID)
			.name(DELETE_COMPANY_NAME)
			.type(CompanyType.SUPPLIER)
			.managerId(REQUESTER_ID)
			.manageHubId(HUB_ID_MISMATCH)
			.address(DELETE_COMPANY_ADDRESS)
			.isDeleted(false)
			.build();

		when(companyRepository.findByIdAndIsDeletedFalse(DELETE_COMPANY_ID)).thenReturn(Optional.of(companyToDelete));
		when(hubClient.isManagerOfHub(REQUESTER_ID)).thenReturn(
			Optional.of(new HubSearchResponseDto(HUB_ID_MATCH, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, REQUESTER_ID))
		);

		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyService.deleteCompany(REQUESTER_ID, requestDto);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION);
		verify(companyRepository).findByIdAndIsDeletedFalse(DELETE_COMPANY_ID);
		verify(companyProductDeletePublisher, never()).publish(any());
	}

	@Description("업체 삭제 테스트 - 실패: 존재하지 않는 업체 ID")
	@Test
	void deleteCompany_404() {
		CompanyDeleteRequestServiceDto requestDto = new CompanyDeleteRequestServiceDto(DELETE_COMPANY_ID);

		when(companyRepository.findByIdAndIsDeletedFalse(DELETE_COMPANY_ID)).thenReturn(Optional.empty());

		CompanyException exception = assertThrows(CompanyException.class, () -> {
			companyService.deleteCompany(REQUESTER_ID, requestDto);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		verify(companyProductDeletePublisher, never()).publish(any());
	}

}