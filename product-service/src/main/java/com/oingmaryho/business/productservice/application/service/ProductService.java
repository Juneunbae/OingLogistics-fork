package com.oingmaryho.business.productservice.application.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.productservice.application.dto.request.ProductDeleteRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductUpdateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductUpdateResponseServiceDto;
import com.oingmaryho.business.productservice.application.mapper.ProductApplicationMapper;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.ProductSearchCriteria;
import com.oingmaryho.business.productservice.domain.repository.CustomProductRepository;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;
import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.exception.ErrorCode;
import com.oingmaryho.business.productservice.exception.ProductException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final CustomProductRepository customProductRepository;
	private final ProductApplicationMapper productApplicationMapper;

	@Transactional
	public ProductCreateResponseServiceDto createProduct(ProductCreateRequestServiceDto productCreateRequestServiceDto){
		Product product = productApplicationMapper.toCreateEntity(productCreateRequestServiceDto);
		Product saveProduct = productRepository.save(product);
		return new ProductCreateResponseServiceDto(saveProduct.getId());
	}

	public Page<ProductSearchResponseServiceDto> searchProducts(ProductSearchRequestServiceDto requestDto, Pageable pageable){
		validateSearchCriteria(createProductSearchCriteria(requestDto));
		Page<Product> products = customProductRepository.findDynamicQuery(createProductSearchCriteria(requestDto),pageable);
		return products.map(productApplicationMapper::toProductSearchResponseServiceDto);
	}

	public ProductDetailsSearchResponseServiceDto getProductDetails(ProductDetailsSearchRequestServiceDto requestDto) {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));
		return productApplicationMapper.toResponseDto(product);
	}

	@Transactional
	public ProductUpdateResponseServiceDto updateProduct(ProductUpdateRequestServiceDto requestServiceDto) {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));

		product.update(
			requestServiceDto.companyName(),
			requestServiceDto.name(),
			requestServiceDto.price(),
			requestServiceDto.stock()
		);
		return productApplicationMapper.toUpdateResponseDto(product.getId());
	}

	@Transactional
	public void deleteProduct(long userId, ProductDeleteRequestServiceDto requestServiceDto) {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));
		product.softDelete(userId);
	}

	private ProductSearchCriteria createProductSearchCriteria(ProductSearchRequestServiceDto requestDto){
		return ProductSearchCriteria.builder()
			.id(requestDto.id())
			.productCode(requestDto.productCode())
			.name(requestDto.name())
			.manageHubId(requestDto.manageHubId())
			.companyID(requestDto.companyId())
			.companyName(requestDto.companyName())
			.minPrice(requestDto.minPrice())
			.maxPrice(requestDto.maxPrice())
			.minStock(requestDto.minStock())
			.maxStock(requestDto.maxStock())
			.build();
	}
	private void validateSearchCriteria(ProductSearchCriteria searchCriteria) {
		if (searchCriteria.getMinPrice() != null && searchCriteria.getMaxPrice() != null) {
			if (searchCriteria.getMinPrice() > searchCriteria.getMaxPrice()) {
				throw new ProductException(ErrorCode.INVALID_PRICE_RANGE);
			}
		}

		if (searchCriteria.getMinStock() != null && searchCriteria.getMaxStock() != null) {
			if (searchCriteria.getMinStock() > searchCriteria.getMaxStock()) {
				throw new ProductException(ErrorCode.INVALID_STOCK_RANGE);
			}
		}
	}


}
