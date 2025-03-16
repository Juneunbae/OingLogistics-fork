package com.oingmaryho.business.companyservice.presentation;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.oingmaryho.business.companyservice.application.dto.request.CompanyCreateRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDeleteRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyDetailsSearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanySearchRequestServiceDto;
import com.oingmaryho.business.companyservice.application.dto.request.CompanyUpdateRequestServiceDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanyCreateRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanySearchRequestDto;
import com.oingmaryho.business.companyservice.presentation.dto.request.CompanyUpdateRequestDto;

@Mapper(componentModel = "spring")
public interface CompanyPresentationMapper {
	CompanyPresentationMapper INSTANCE = Mappers.getMapper(CompanyPresentationMapper.class);

	CompanyCreateRequestServiceDto toCreateServiceDto(CompanyCreateRequestDto companyCreateRequestDto);

	CompanyDetailsSearchRequestServiceDto toDetailsSearchServiceDto(UUID id);

	CompanySearchRequestServiceDto toSearchServiceDto(CompanySearchRequestDto companySearchRequestDto, Pageable pageable);

	@Mapping(target="id",source="id")
	CompanyUpdateRequestServiceDto toUpdateServiceDto(UUID id, CompanyUpdateRequestDto companyUpdateRequestDto);

	CompanyDeleteRequestServiceDto toDeleteServiceDto(UUID id);
}
