package com.oingmaryho.business.companyservice.application.dto.mapper;


import org.mapstruct.Mapper;

import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyCreateResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyUpdateResponseServiceDto;
import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyCreateResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanySearchResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface CompanyApplicationMapper {
	CompanyCreateResponseDto toCreateResponseDto(CompanyCreateResponseServiceDto companyCreateResponseServiceDto);

	CompanyDetailsSearchResponseServiceDto toResponseDto(Company company);

	CompanySearchResponseDto toSearchResponseDto(CompanySearchResponseServiceDto companySearchResponseServiceDto);

	CompanyUpdateResponseDto toUpdateResponseDto(CompanyUpdateResponseServiceDto companyUpdateResponseServiceDto);

	Company toCompanyEntity(CompanyDetailsSearchRequestServiceDto companyDetailsSearchRequestServiceDto);
}
