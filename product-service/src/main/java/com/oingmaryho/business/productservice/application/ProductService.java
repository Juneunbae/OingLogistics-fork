package com.oingmaryho.business.productservice.application;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final ProductApplicationMapper productApplicationMapper;

	public ProductDetailsSearchResponseServiceDto getProductDetails(ProductDetailsSearchRequestServiceDto requestDto) {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + requestDto.id()));

		return productApplicationMapper.toResponseDto(product);
	}
}
