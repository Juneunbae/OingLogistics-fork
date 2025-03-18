package com.oingmaryho.business.companyservice.application.dto.mapper;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

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

	CompanySearchResponseServiceDto toCompanySearchResponseServiceDto(Company company);

	CompanyUpdateResponseDto toUpdateResponseDto(CompanyUpdateResponseServiceDto companyUpdateResponseServiceDto);

	Company toCompanyEntity(CompanyDetailsSearchRequestServiceDto companyDetailsSearchRequestServiceDto);
}
