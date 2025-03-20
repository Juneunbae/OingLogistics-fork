package com.oingmaryho.business.productservice.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.productservice.application.mapper.ProductApplicationMapper;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.CustomProductRepository;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;
import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	// private final CustomProductRepository customProductRepository;
	private final ProductApplicationMapper productApplicationMapper;

	@Transactional
	public ProductCreateResponseServiceDto createProduct(ProductCreateRequestServiceDto productCreateRequestServiceDto){
		Product product = productApplicationMapper.toCreateEntity(productCreateRequestServiceDto);
		Product saveProduct = productRepository.save(product);
		return new ProductCreateResponseServiceDto(saveProduct.getId());
	}
}
