package com.oingmaryho.business.productservice.presentation.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductDetailsSearchResponseDto;

@Mapper(componentModel = "spring")
public interface ProductPresentationMapper {
	// in : presentation request -> service request
	ProductDetailsSearchRequestServiceDto toProductServiceDto(UUID id);

	// out : service response -> presentation response
	ProductDetailsSearchResponseDto toProductDto(ProductDetailsSearchResponseServiceDto productServiceDto);
}
