package com.oingmaryho.business.companyservice.application.dto.mapper;


import java.util.UUID;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyCreateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyUpdateResponseServiceDto;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyCreateResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface CompanyApplicationMapper {
	CompanyCreateResponseDto toCreateResponseDto(CompanyCreateResponseServiceDto companyCreateResponseServiceDto);

	@BeanMapping(ignoreByDefault = true)
	CompanyDetailsSearchResponseServiceDto toResponseDto(Company company);

	@BeanMapping(ignoreByDefault = true)
	CompanySearchResponseServiceDto toCompanySearchResponseServiceDto(Company company);

	CompanyUpdateResponseServiceDto toUpdateResponseDto(UUID id);

	@Mapping(target = "deletedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	@Mapping(target = "id", ignore = true)
	Company toCreateEntity(CompanyCreateRequestServiceDto companyCreateRequestServiceDto, UUID manageHubId);
}
