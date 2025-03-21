package com.oingmaryho.business.productservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductUpdateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductUpdateResponseServiceDto;
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

	private Product product;
	private static final UUID FIXED_PRODUCT_ID = UUID.fromString("222e2222-e89b-12d3-a456-426614174000");
	private static final UUID FIXED_COMPANY_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	private static final UUID FIXED_MANAGE_HUB_ID = UUID.fromString("000e0000-e89b-12d3-a456-426614174000");
	private UUID productId;

	@BeforeEach
	void setUp() {
		productId = FIXED_PRODUCT_ID;
		product = Product.builder()
			.id(FIXED_COMPANY_ID)
			.productCode("C0-1")
			.name("Test Company")
			.companyId(FIXED_COMPANY_ID)
			.manageHubId(FIXED_MANAGE_HUB_ID)
			.price(1L)
			.stock(2L)
			.isDeleted(false)
			.build();
	}

	@Test
	@Description("상품 등록 테스트")
	void createProduct() {
		// Given
		ProductCreateRequestServiceDto requestDto = new ProductCreateRequestServiceDto(
			FIXED_COMPANY_ID,
			"C0-1",
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

	@Test
	@Description("상품 검색 테스트 - 페이지네이션 포함")
	void searchProducts() {
		Pageable pageable = mock(Pageable.class);
		ProductSearchRequestServiceDto requestDto = mock(ProductSearchRequestServiceDto.class);
		Page<Product> productPage = new PageImpl<>(List.of(product));
		when(customProductRepository.findDynamicQuery(any(),eq(pageable))).thenReturn(productPage);
		when(productApplicationMapper.toProductSearchResponseServiceDto(any())).thenReturn(mock(
			ProductSearchResponseServiceDto.class));

		Page<ProductSearchResponseServiceDto> response = productService.searchProducts(requestDto, pageable);

		assertThat(response).isNotNull();
		assertThat(response.getTotalElements()).isEqualTo(1);
	}

	@Test
	@Description("상품 조회 테스트 - ID로 검색")
	void getProductDetails() {
		// Given
		ProductDetailsSearchRequestServiceDto requestDto = new ProductDetailsSearchRequestServiceDto(productId);

		ProductDetailsSearchResponseServiceDto expectedResponse = new ProductDetailsSearchResponseServiceDto(
			productId, "Test product", FIXED_COMPANY_ID, "Company Name", FIXED_MANAGE_HUB_ID, 1L, 2L
		);

		when(productRepository.findByIdAndIsDeletedFalse(productId)).thenReturn(Optional.of(product));
		when(productApplicationMapper.toResponseDto(product)).thenReturn(expectedResponse);

		// When
		ProductDetailsSearchResponseServiceDto response = productService.getProductDetails(requestDto);

		// Then
		System.out.println("상품 정보 : " + response.name());
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(expectedResponse.id());
	}

	@Test
	@Description("상품 수정 테스트")
	void updateProduct() {
		ProductUpdateRequestServiceDto requestDto = new ProductUpdateRequestServiceDto(
			productId, "Updated company name", "Updated product name", 2L, 1L
		);

		when(productRepository.findByIdAndIsDeletedFalse(productId)).thenReturn(Optional.of(product));
		when(productApplicationMapper.toUpdateResponseDto(any(UUID.class)))
			.thenReturn(new ProductUpdateResponseServiceDto(FIXED_PRODUCT_ID));

		ProductUpdateResponseServiceDto response = productService.updateProduct(requestDto);

		assertThat(product.getName()).isEqualTo("Updated product name");
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(productId);
	}

}
