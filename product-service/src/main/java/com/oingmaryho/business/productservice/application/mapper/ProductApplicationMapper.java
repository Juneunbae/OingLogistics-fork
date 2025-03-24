package com.oingmaryho.business.productservice.application.mapper;

import java.util.UUID;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductUpdateResponseServiceDto;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.presentation.dto.response.CompanyDetailsSearchResponseDto;

@Mapper(componentModel = "spring")
public interface ProductApplicationMapper {

	// in : application request dto -> entity
	@Mapping(target = "deletedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "companyName", target = "companyName")
	@Mapping(source = "manageHubId", target = "manageHubId")
	Product toCreateEntity(ProductCreateRequestServiceDto productCreateRequestServiceDto, String companyName, UUID manageHubId);

	// out : entity -> application response dto
	@BeanMapping(ignoreByDefault = true)
	ProductDetailsSearchResponseServiceDto toResponseDto(Product product);

	@BeanMapping(ignoreByDefault = true)
	ProductSearchResponseServiceDto toProductSearchResponseServiceDto(Product product);

	ProductUpdateResponseServiceDto toUpdateResponseDto(UUID id);
}
