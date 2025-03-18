package com.oingmaryho.business.companyservice.presentation.dto.mapper;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDeleteRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanyDetailsSearchResponseServiceDto;
import com.oingmaryho.business.companyservice.application.dto.response.CompanySearchResponseServiceDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanyCreateRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanySearchRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanyUpdateRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.companyservice.presentation.dto.response.CompanySearchResponseDto;

@Mapper(componentModel = "spring")
public interface CompanyPresentationMapper {

	CompanyCreateRequestServiceDto toCreateServiceDto(CompanyCreateRequestDto companyCreateRequestDto);

	CompanyDetailsSearchRequestServiceDto toDetailsSearchServiceDto(UUID id);

	CompanySearchRequestServiceDto toCompanySearchResponseServiceDto(CompanySearchRequestDto companySearchRequestDto);

	CompanyUpdateRequestServiceDto toUpdateServiceDto(UUID id, CompanyUpdateRequestDto companyUpdateRequestDto);

	CompanyDeleteRequestServiceDto toDeleteServiceDto(UUID id);

	CompanyDetailsSearchResponseDto toDetailsSearchResponseDto(CompanyDetailsSearchResponseServiceDto responseServiceDto);

	CompanySearchResponseDto toCompanySearchResponseDto(CompanySearchResponseServiceDto responseServiceDto);
}
