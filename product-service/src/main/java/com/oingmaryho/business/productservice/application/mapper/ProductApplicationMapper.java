package com.oingmaryho.business.productservice.application.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.domain.Product;

@Mapper(componentModel = "spring")
public interface ProductApplicationMapper {

	// in : application request dto -> entity

	// out : entity -> application response dto
	@BeanMapping(ignoreByDefault = true)
	ProductDetailsSearchResponseServiceDto toResponseDto(Product product);
}
