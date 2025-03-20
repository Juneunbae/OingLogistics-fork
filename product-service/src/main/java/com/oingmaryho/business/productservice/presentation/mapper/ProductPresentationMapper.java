package com.oingmaryho.business.productservice.presentation.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.presentation.dto.request.ProductCreateRequestDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductCreateResponseDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductDetailsSearchResponseDto;

@Mapper(componentModel = "spring")
public interface ProductPresentationMapper {
	// in : presentation request -> service request
	ProductDetailsSearchRequestServiceDto toDetailsSearchServiceDto(UUID id);
	ProductCreateRequestServiceDto toCreateServiceDto(ProductCreateRequestDto productCreateRequestDto);

	// out : service response -> presentation response
	ProductDetailsSearchResponseDto toDetailsSearchDto(ProductDetailsSearchResponseServiceDto productServiceDto);
	ProductCreateResponseDto toCreateDto(ProductCreateResponseServiceDto responseServiceDto);
}
