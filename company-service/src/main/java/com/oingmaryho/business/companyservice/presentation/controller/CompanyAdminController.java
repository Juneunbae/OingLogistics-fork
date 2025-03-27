package com.oingmaryho.business.companyservice.presentation.controller;

import java.util.UUID;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.common.infrastructure.annotation.RequiredRoles;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDeleteRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyCreateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyUpdateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.service.CompanyAdminService;
import com.oingmaryho.business.companyservice.application.service.CompanyService;
import com.oingmaryho.business.companyservice.domain.CompanyType;
import com.oingmaryho.business.companyservice.presentation.dto.mapper.CompanyPresentationMapper;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanyCreateRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanySearchRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanyUpdateRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyCreateResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanySearchResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyUpdateResponseDto;
import com.oingmaryho.business.companyservice.utils.PageableUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/companies")
public class CompanyAdminController {
	private final CompanyAdminService companyService;
	private final CompanyPresentationMapper companyPresentationMapper;

	@Description("마스터 - 업체 생성")
	@RequiredRoles(UserRoleType.MASTER)
	@PostMapping
	public ResponseEntity<CompanyCreateResponseDto> createCompany(
		@RequestBody CompanyCreateRequestDto companyCreateRequestDto
	) {
		CompanyCreateRequestServiceDto requestServiceDto = companyPresentationMapper.toCreateServiceDto(companyCreateRequestDto);
		CompanyCreateResponseServiceDto responseServiceDto = companyService.createCompany(requestServiceDto);
		CompanyCreateResponseDto responseDto = companyPresentationMapper.toCreateDto(responseServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("마스터 - 업체 전체 조회")
	@RequiredRoles(UserRoleType.MASTER)
	@GetMapping
	public ResponseEntity<Page<CompanySearchResponseDto>> getCompanies(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "10") int size,
		@RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
		@RequestParam(name = "by", defaultValue = "name") String by,
		@RequestParam(value = "id", required = false) UUID id,
		@RequestParam(name = "type", required = false) CompanyType type,
		@RequestParam(name = "name", required = false) String name,
		@RequestParam(name = "managerId", required = false) Long managerId,
		@RequestParam(name = "manageHubId", required = false) UUID manageHubId,
		@RequestParam(name = "address", required = false) String address,
		@RequestParam(name = "isDeleted", required = false) Boolean isDeleted
	) {

		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		CompanySearchRequestDto requestDto = new CompanySearchRequestDto(id, type, name, managerId,manageHubId, address,isDeleted);

		Page<CompanySearchResponseServiceDto> responseDto = companyService.searchCompanies(companyPresentationMapper.toCompanySearchResponseServiceDto(requestDto),pageable);
		return ResponseEntity.ok(responseDto.map(companyPresentationMapper::toCompanySearchResponseDto));
	}

	@Description("마스터 - 업체 상세 조회")
	@RequiredRoles(UserRoleType.MASTER)
	@GetMapping("/{id}")
	public ResponseEntity<CompanyDetailsSearchResponseDto> getCompanyById(
		@PathVariable UUID id
		) {
		CompanyDetailsSearchRequestServiceDto requestServiceDto = companyPresentationMapper.toDetailsSearchServiceDto(id);
		CompanyDetailsSearchResponseServiceDto responseServiceDto = companyService.getCompanyById(requestServiceDto);
		CompanyDetailsSearchResponseDto responseDto = companyPresentationMapper.toDetailsSearchResponseDto(responseServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("마스터 - 업체 수정")
	@RequiredRoles(UserRoleType.MASTER)
	@PutMapping("/{id}")
	public ResponseEntity<CompanyUpdateResponseDto> updateCompany(
		@PathVariable UUID id,
		@RequestBody CompanyUpdateRequestDto companyUpdateRequestDto
		) {

		CompanyUpdateRequestServiceDto requestServiceDto = companyPresentationMapper.toUpdateServiceDto(id,companyUpdateRequestDto);
		CompanyUpdateResponseServiceDto responseServiceDto = companyService.updateCompany(requestServiceDto);
		CompanyUpdateResponseDto responseDto = companyPresentationMapper.toUpdateResponseDto(responseServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("마스터 - 업체 삭제")
	@RequiredRoles(UserRoleType.MASTER)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCompany(
		@PathVariable UUID id,
		HttpServletRequest request
	) {
		Long userId = (Long) request.getAttribute("userId");
		CompanyDeleteRequestServiceDto requestServiceDto = companyPresentationMapper.toDeleteServiceDto(id);
		companyService.deleteCompany(userId, requestServiceDto);
		return ResponseEntity.noContent().build();
	}

}
