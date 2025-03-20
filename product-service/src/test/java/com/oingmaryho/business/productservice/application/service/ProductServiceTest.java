package com.oingmaryho.business.productservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.application.mapper.ProductApplicationMapper;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.CustomProductRepository;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CustomProductRepository customProductRepository;

	@Mock
	private ProductApplicationMapper productApplicationMapper;

	@InjectMocks
	private ProductService productService;

	private static final UUID FIXED_PRODUCT_ID = UUID.fromString("222e2222-e89b-12d3-a456-426614174000");
	private static final UUID FIXED_COMPANY_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	private static final UUID FIXED_MANAGE_HUB_ID = UUID.fromString("000e0000-e89b-12d3-a456-426614174000");

	@Test
	@Description("상품 등록 테스트")
	void createProduct() {
		// Given
		ProductCreateRequestServiceDto requestDto = new ProductCreateRequestServiceDto(
			FIXED_COMPANY_ID,
			"Test product",
			FIXED_MANAGE_HUB_ID,
			1L,
			2L
		);

		Product product = Product.builder()
			.id(FIXED_PRODUCT_ID)
			.companyId(FIXED_COMPANY_ID)
			.name("Test product")
			.manageHubId(FIXED_MANAGE_HUB_ID)
			.stock(1L)
			.price(2L)
			.isDeleted(false)
			.build();

		ProductCreateResponseServiceDto expectedResponseDto = new ProductCreateResponseServiceDto(FIXED_PRODUCT_ID);

		when(productApplicationMapper.toCreateEntity(any(ProductCreateRequestServiceDto.class))).thenReturn(product);
		when(productRepository.save(any(Product.class))).thenReturn(product);

		// When
		ProductCreateResponseServiceDto actualResponseDto = productService.createProduct(requestDto);
		System.out.println("등록된 상품 정보: " + actualResponseDto.id());

		// Then
		assertThat(actualResponseDto).isNotNull();
		assertThat(actualResponseDto.id()).isEqualTo(expectedResponseDto.id());
	}
}
