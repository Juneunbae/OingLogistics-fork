package com.oingmaryho.business.productservice.presentation.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oingmaryho.business.productservice.application.dto.request.ProductDeleteRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductUpdateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductUpdateResponseServiceDto;
import com.oingmaryho.business.productservice.presentation.dto.request.ProductCreateRequestDto;
import com.oingmaryho.business.productservice.presentation.dto.request.ProductSearchRequestDto;
import com.oingmaryho.business.productservice.presentation.dto.request.ProductUpdateRequestDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductCreateResponseDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductDetailsSearchResponseDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductSearchResponseDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface ProductPresentationMapper {
	// in : presentation request -> service request
	ProductDetailsSearchRequestServiceDto toDetailsSearchServiceDto(UUID id);
	ProductCreateRequestServiceDto toCreateServiceDto(ProductCreateRequestDto productCreateRequestDto);
	ProductSearchRequestServiceDto toProductSearchResponseServiceDto(ProductSearchRequestDto productSearchRequestDto);
	ProductUpdateRequestServiceDto toUpdateServiceDto(UUID id, ProductUpdateRequestDto productUpdateRequestDto);
	ProductDeleteRequestServiceDto toDeleteServiceDto(UUID id);

	// out : service response -> presentation response
	ProductDetailsSearchResponseDto toDetailsSearchDto(ProductDetailsSearchResponseServiceDto productServiceDto);
	ProductCreateResponseDto toCreateDto(ProductCreateResponseServiceDto responseServiceDto);
	ProductSearchResponseDto toProductSearchResponseDto(ProductSearchResponseServiceDto responseServiceDto);
	ProductUpdateResponseDto toUpdateResponseDto(ProductUpdateResponseServiceDto responseServiceDto);


}
