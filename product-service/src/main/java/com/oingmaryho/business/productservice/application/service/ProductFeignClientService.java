package com.oingmaryho.business.productservice.application.service;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.mapper.ProductApplicationMapper;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;
import com.oingmaryho.business.productservice.exception.ErrorCode;
import com.oingmaryho.business.productservice.exception.ProductException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductFeignClientService {
	private final ProductRepository productRepository;
	private final ProductApplicationMapper productApplicationMapper;

	public ProductDetailsSearchResponseServiceDto getProduct(ProductDetailsSearchRequestServiceDto requestDto) {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));
		return productApplicationMapper.toResponseDto(product);
	}
}

