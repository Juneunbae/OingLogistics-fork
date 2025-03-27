package com.oingmaryho.business.productservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductDeleteRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductUpdateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductUpdateResponseServiceDto;
import com.oingmaryho.business.productservice.application.mapper.ProductApplicationMapper;
import com.oingmaryho.business.productservice.application.service.feignClient.CompanyClient;
import com.oingmaryho.business.productservice.application.service.feignClient.HubClient;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.CustomProductRepository;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;
import com.oingmaryho.business.productservice.exception.ErrorCode;
import com.oingmaryho.business.productservice.exception.ProductException;
import com.oingmaryho.business.productservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.productservice.presentation.dto.response.HubSearchResponseDto;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock private ProductRepository productRepository;
	@Mock private CustomProductRepository customProductRepository;
	@Mock private ProductApplicationMapper productApplicationMapper;
	@Mock private CompanyClient companyClient;
	@Mock private HubClient hubClient;

	@InjectMocks private ProductService productService;

	private Product product;

	private static final UUID PRODUCT_ID = UUID.fromString("a36a97cc-e1fb-42a5-8871-1a146c9f2329");
	private static final String PRODUCT_CODE = "C0-14";
	private static final String PRODUCT_NAME = "상품쪽 리팩토링 상품- 허브매니저가 생성";
	private static final Integer PRICE = 12000;
	private static final Integer STOCK = 1000;

	private static final String UPDATED_PRODUCT_NAME_BY_HUB = "허브에 의해 수정된 상품명";
	private static final int UPDATED_PRICE_BY_HUB = 15000;
	private static final int UPDATED_STOCK_BY_HUB = 500;

	private static final String UPDATED_PRODUCT_NAME_BY_COMPANY = "업체에 의해 수정된 상품명";
	private static final int UPDATED_PRICE_BY_COMPANY = 16000;
	private static final int UPDATED_STOCK_BY_COMPANY = 300;

	private static final String INVALID_UPDATE_NAME = "수정 불가 상품";
	private static final int INVALID_UPDATE_PRICE = 9999;
	private static final int INVALID_UPDATE_STOCK = 1;




	private static final UUID COMPANY_ID = UUID.fromString("1ebee6c4-5bd8-40c5-a6b4-0b0e8764a269");
	private static final Long COMPANY_MANAGER_ID = 5L;
	private static final String COMPANY_NAME = "리팩토링업체";
	private static final UUID COMPANY_MANAGE_HUB_ID = UUID.fromString("b2341dfa-2e1e-4cb2-a386-6e2d67f02d83");
	private static final String COMPANY_ADDRESS = "경기도 고양시 덕양구 355-11";
	private static final String COMPANY_TYPE_SUPPLIER = "SUPPLIER";
	private static final String COMPANY_TYPE_RECEIVER = "RECEIVER";

	private static final UUID OTHER_COMPANY_ID = UUID.randomUUID();
	private static final String OTHER_COMPANY_NAME = "다른 업체";
	private static final String OTHER_COMPANY_ADDRESS = "다른 주소";

	private static final String HUB_NAME = "경기 북부 센터";
	private static final String HUB_ADDRESS = "경기도 고양시 덕양구 권율대로 570";
	private static final Double HUB_LATITUDE = 0.0;
	private static final Double HUB_LONGITUDE = 0.0;
	private static final Long HUB_MANAGER_ID = 7L;

	private static final UUID OTHER_HUB_ID = UUID.randomUUID();
	private static final String OTHER_HUB_NAME = "다른 허브";
	private static final String OTHER_HUB_ADDRESS = "다른 주소";
	private static final Double OTHER_HUB_LATITUDE = 10.0;
	private static final Double OTHER_HUB_LONGITUDE = 10.0;

	@BeforeEach
	void setUp() {
		product = Product.builder()
			.id(PRODUCT_ID)
			.productCode(PRODUCT_CODE)
			.name(PRODUCT_NAME)
			.companyId(COMPANY_ID)
			.companyName(COMPANY_NAME)
			.manageHubId(COMPANY_MANAGE_HUB_ID)
			.price(PRICE)
			.stock(STOCK)
			.isDeleted(false)
			.build();
	}

	@Test
	@Description("상품 등록 테스트 - 성공")
	void createProduct() {
		// given
		ProductCreateRequestServiceDto requestDto = new ProductCreateRequestServiceDto(
			COMPANY_ID,
			PRODUCT_CODE,
			PRODUCT_NAME,
			PRICE,
			STOCK
		);
		CompanyDetailsSearchResponseDto company = new CompanyDetailsSearchResponseDto(
			COMPANY_ID, COMPANY_NAME, COMPANY_MANAGER_ID, COMPANY_TYPE_SUPPLIER, COMPANY_MANAGE_HUB_ID, COMPANY_ADDRESS
		);

		HubSearchResponseDto hub = new HubSearchResponseDto(
			COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, HUB_MANAGER_ID
		);

		when(companyClient.getCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));
		when(hubClient.getHubById(COMPANY_MANAGE_HUB_ID)).thenReturn(Optional.of(hub));
		when(productRepository.existsByProductCodeAndIsDeletedFalse(PRODUCT_CODE)).thenReturn(false);
		when(productApplicationMapper.toCreateEntity(requestDto, COMPANY_NAME, COMPANY_MANAGE_HUB_ID)).thenReturn(product);
		when(productRepository.save(product)).thenReturn(product);

		// when
		ProductCreateResponseServiceDto response = productService.createProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER);

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(PRODUCT_ID);
	}

	@Test
	@Description("상품 등록 실패 - 업체 없음")
	void createProduct_companyNotFound() {
		// given
		ProductCreateRequestServiceDto requestDto = new ProductCreateRequestServiceDto(
			COMPANY_ID,
			PRODUCT_CODE,
			PRODUCT_NAME,
			STOCK,
			PRICE
		);

		when(companyClient.getCompanyById(COMPANY_ID)).thenReturn(Optional.empty());

		// when & then
		ProductException exception = assertThrows(ProductException.class, () ->
			productService.createProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMPANY_NOT_FOUND);
	}

	@Test
	@Description("상품 등록 실패 - 허브 없음")
	void createProduct_hubNotFound() {
		ProductCreateRequestServiceDto requestDto = new ProductCreateRequestServiceDto(
			COMPANY_ID,
			PRODUCT_CODE,
			PRODUCT_NAME,
			STOCK,
			PRICE
		);

		CompanyDetailsSearchResponseDto company = new CompanyDetailsSearchResponseDto(
			COMPANY_ID, COMPANY_NAME, COMPANY_MANAGER_ID, COMPANY_TYPE_SUPPLIER, COMPANY_MANAGE_HUB_ID, COMPANY_ADDRESS
		);

		when(companyClient.getCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));
		when(hubClient.getHubById(COMPANY_MANAGE_HUB_ID)).thenReturn(Optional.empty());

		ProductException exception = assertThrows(ProductException.class, () ->
			productService.createProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HUB_NOT_FOUND);
	}

	@Test
	@Description("상품 등록 실패 - 업체 타입이 SUPPLIER 가 아님")
	void createProduct_invalidCompanyType() {
		ProductCreateRequestServiceDto requestDto = new ProductCreateRequestServiceDto(
			COMPANY_ID,
			PRODUCT_CODE,
			PRODUCT_NAME,
			STOCK,
			PRICE
		);

		CompanyDetailsSearchResponseDto company = new CompanyDetailsSearchResponseDto(
			COMPANY_ID, COMPANY_NAME, COMPANY_MANAGER_ID, COMPANY_TYPE_RECEIVER, COMPANY_MANAGE_HUB_ID, COMPANY_ADDRESS
		);

		HubSearchResponseDto hub = new HubSearchResponseDto(
			COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, HUB_MANAGER_ID
		);

		when(companyClient.getCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));
		when(hubClient.getHubById(COMPANY_MANAGE_HUB_ID)).thenReturn(Optional.of(hub));

		ProductException exception = assertThrows(ProductException.class, () ->
			productService.createProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_COMPANY_TYPE);
	}

	@Test
	@Description("상품 등록 실패 - 상품 코드 중복")
	void createProduct_duplicateProductCode() {
		ProductCreateRequestServiceDto requestDto = new ProductCreateRequestServiceDto(
			COMPANY_ID,
			PRODUCT_CODE,
			PRODUCT_NAME,
			STOCK,
			PRICE
		);

		CompanyDetailsSearchResponseDto company = new CompanyDetailsSearchResponseDto(
			COMPANY_ID, COMPANY_NAME, COMPANY_MANAGER_ID, COMPANY_TYPE_SUPPLIER, COMPANY_MANAGE_HUB_ID, COMPANY_ADDRESS
		);

		HubSearchResponseDto hub = new HubSearchResponseDto(
			COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, HUB_MANAGER_ID
		);

		when(companyClient.getCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));
		when(hubClient.getHubById(COMPANY_MANAGE_HUB_ID)).thenReturn(Optional.of(hub));
		when(productRepository.existsByProductCodeAndIsDeletedFalse(PRODUCT_CODE)).thenReturn(true);

		ProductException exception = assertThrows(ProductException.class, () ->
			productService.createProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_REGISTERED_PRODUCT);
	}

	@Test
	@Description("상품 상세 조회 - 성공 (허브 매니저)")
	void getProductDetails_success() {
		ProductDetailsSearchRequestServiceDto requestDto = new ProductDetailsSearchRequestServiceDto(PRODUCT_ID);

		HubSearchResponseDto hub = new HubSearchResponseDto(
			COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, HUB_MANAGER_ID
		);

		ProductDetailsSearchResponseServiceDto responseDto = new ProductDetailsSearchResponseServiceDto(
			PRODUCT_ID, PRODUCT_NAME, COMPANY_ID, COMPANY_NAME, COMPANY_MANAGE_HUB_ID, PRICE, STOCK
		);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(HUB_MANAGER_ID)).thenReturn(Optional.of(hub));
		when(productApplicationMapper.toResponseDto(product)).thenReturn(responseDto);

		ProductDetailsSearchResponseServiceDto result = productService.getProductDetails(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(PRODUCT_ID);
		assertThat(result.name()).isEqualTo(PRODUCT_NAME);
	}

	@Test
	@Description("상품 상세 조회 실패 - 상품 없음")
	void getProductDetails_notFound() {
		ProductDetailsSearchRequestServiceDto requestDto = new ProductDetailsSearchRequestServiceDto(PRODUCT_ID);
		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.empty());

		ProductException exception = assertThrows(ProductException.class, () ->
			productService.getProductDetails(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
	}

	@Test
	@Description("상품 상세 조회 실패 - 허브 정보 없음")
	void getProductDetails_hubNotFound() {
		ProductDetailsSearchRequestServiceDto requestDto = new ProductDetailsSearchRequestServiceDto(PRODUCT_ID);
		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(HUB_MANAGER_ID)).thenReturn(Optional.empty());

		ProductException exception = assertThrows(ProductException.class, () ->
			productService.getProductDetails(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HUB_NOT_FOUND);
	}

	@Test
	@Description("상품 검색 테스트 - 성공")
	void searchProducts_success() {
		// given
		ProductSearchRequestServiceDto requestDto = new ProductSearchRequestServiceDto(
			null, null, null, null, COMPANY_ID, COMPANY_NAME, null, null, null, null, false
		);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> productPage = new PageImpl<>(List.of(product));

		when(customProductRepository.findDynamicQuery(any(), eq(pageable))).thenReturn(productPage);
		when(productApplicationMapper.toProductSearchResponseServiceDto(any()))
			.thenReturn(new ProductSearchResponseServiceDto(
				product.getId(),
				product.getProductCode(),
				product.getName(),
				product.getManageHubId(),
				product.getCompanyId(),
				product.getCompanyName(),
				product.getPrice(),
				product.getStock()
			));

		// when
		Page<ProductSearchResponseServiceDto> result = productService.searchProducts(requestDto, pageable, COMPANY_MANAGER_ID, UserRoleType.COMPANY_MANAGER);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).name()).isEqualTo(PRODUCT_NAME);
	}

	@Test
	@Description("상품 검색 성공 - 허브 매니저는 본인 허브 상품만 조회")
	void searchProducts_success_forHubManager() {
		// given
		ProductSearchRequestServiceDto requestDto = new ProductSearchRequestServiceDto(
			null, null, null, null, null, null, null, null, null, null, false
		);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> productPage = new PageImpl<>(List.of(product));

		HubSearchResponseDto myHub = new HubSearchResponseDto(
			COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, HUB_MANAGER_ID
		);

		when(hubClient.isManagerOfHub(HUB_MANAGER_ID)).thenReturn(Optional.of(myHub));
		when(customProductRepository.findDynamicQuery(any(), eq(pageable))).thenReturn(productPage);
		when(productApplicationMapper.toProductSearchResponseServiceDto(any()))
			.thenReturn(new ProductSearchResponseServiceDto(
				product.getId(),
				product.getProductCode(),
				product.getName(),
				product.getManageHubId(),
				product.getCompanyId(),
				product.getCompanyName(),
				product.getPrice(),
				product.getStock()
			));

		// when
		Page<ProductSearchResponseServiceDto> result = productService.searchProducts(requestDto, pageable, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).manageHubId()).isEqualTo(COMPANY_MANAGE_HUB_ID);
	}

	@Test
	@Description("상품 검색 실패 - 최소 가격이 최대 가격보다 큼")
	void searchProducts_invalidPriceRange() {
		// given
		ProductSearchRequestServiceDto requestDto = new ProductSearchRequestServiceDto(
			null, null, null, null, COMPANY_ID, COMPANY_NAME, 5000, 1000, null, null, false
		);
		Pageable pageable = PageRequest.of(0, 10);

		// when & then
		ProductException exception = assertThrows(ProductException.class, () ->
			productService.searchProducts(requestDto, pageable, 5L, UserRoleType.COMPANY_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PRICE_RANGE);
	}

	@Test
	@Description("상품 수정 성공 - 허브 매니저가 본인 허브 상품 수정")
	void updateProduct_success_byHubManager() {
		// given
		ProductUpdateRequestServiceDto requestDto = new ProductUpdateRequestServiceDto(
			PRODUCT_ID, UPDATED_PRODUCT_NAME_BY_HUB, COMPANY_MANAGE_HUB_ID, UPDATED_PRICE_BY_HUB, UPDATED_STOCK_BY_HUB
		);

		HubSearchResponseDto hubDto = new HubSearchResponseDto(
			COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, HUB_MANAGER_ID
		);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(HUB_MANAGER_ID)).thenReturn(Optional.of(hubDto));
		when(productApplicationMapper.toUpdateResponseDto(PRODUCT_ID)).thenReturn(new ProductUpdateResponseServiceDto(PRODUCT_ID));

		// when
		ProductUpdateResponseServiceDto response = productService.updateProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER);

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(PRODUCT_ID);
	}

	@Test
	@Description("상품 수정 성공 - 업체 매니저가 본인 업체 상품 수정")
	void updateProduct_success_byCompanyManager() {
		// given
		ProductUpdateRequestServiceDto requestDto = new ProductUpdateRequestServiceDto(
			PRODUCT_ID, UPDATED_PRODUCT_NAME_BY_COMPANY, COMPANY_MANAGE_HUB_ID, UPDATED_PRICE_BY_COMPANY, UPDATED_STOCK_BY_COMPANY
		);

		CompanyDetailsSearchResponseDto companyDto = new CompanyDetailsSearchResponseDto(
			COMPANY_ID, COMPANY_NAME, COMPANY_MANAGER_ID, COMPANY_TYPE_SUPPLIER, COMPANY_MANAGE_HUB_ID, COMPANY_ADDRESS
		);

		HubSearchResponseDto dummyHub = new HubSearchResponseDto(
			COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, HUB_MANAGER_ID
		);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(COMPANY_MANAGER_ID)).thenReturn(Optional.of(dummyHub));
		when(companyClient.getCompanyByManagerId(COMPANY_MANAGER_ID)).thenReturn(Optional.of(companyDto));
		when(productApplicationMapper.toUpdateResponseDto(PRODUCT_ID)).thenReturn(new ProductUpdateResponseServiceDto(PRODUCT_ID));

		// when
		ProductUpdateResponseServiceDto response = productService.updateProduct(requestDto, COMPANY_MANAGER_ID, UserRoleType.COMPANY_MANAGER);

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(PRODUCT_ID);
	}

	@Test
	@Description("상품 수정 실패 - 업체 매니저가 다른 업체 상품 수정 시도")
	void updateProduct_fail_companyManagerOtherCompany() {
		ProductUpdateRequestServiceDto requestDto = new ProductUpdateRequestServiceDto(
			PRODUCT_ID, INVALID_UPDATE_NAME, COMPANY_MANAGE_HUB_ID, INVALID_UPDATE_PRICE, INVALID_UPDATE_STOCK
		);

		CompanyDetailsSearchResponseDto otherCompany = new CompanyDetailsSearchResponseDto(
			OTHER_COMPANY_ID, OTHER_COMPANY_NAME, COMPANY_MANAGER_ID, COMPANY_TYPE_SUPPLIER, COMPANY_MANAGE_HUB_ID, OTHER_COMPANY_ADDRESS
		);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(COMPANY_MANAGER_ID)).thenReturn(Optional.of(new HubSearchResponseDto(COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, COMPANY_MANAGER_ID)));
		when(companyClient.getCompanyByManagerId(COMPANY_MANAGER_ID)).thenReturn(Optional.of(otherCompany));

		ProductException exception = assertThrows(ProductException.class, () ->
			productService.updateProduct(requestDto, COMPANY_MANAGER_ID, UserRoleType.COMPANY_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION);
	}

	@Test
	@Description("상품 수정 실패 - 허브 매니저가 다른 허브의 상품 수정 시도")
	void updateProduct_fail_hubManagerOtherHub() {
		ProductUpdateRequestServiceDto requestDto = new ProductUpdateRequestServiceDto(
			PRODUCT_ID, INVALID_UPDATE_NAME, COMPANY_MANAGE_HUB_ID, INVALID_UPDATE_PRICE, INVALID_UPDATE_STOCK
		);

		UUID otherHubId = UUID.randomUUID();
		HubSearchResponseDto otherHub = new HubSearchResponseDto(OTHER_HUB_ID, OTHER_HUB_NAME, OTHER_HUB_ADDRESS, OTHER_HUB_LATITUDE, OTHER_HUB_LONGITUDE, HUB_MANAGER_ID);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(HUB_MANAGER_ID)).thenReturn(Optional.of(otherHub));

		ProductException exception = assertThrows(ProductException.class, () ->
			productService.updateProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION);
	}

	@Test
	@Description("상품 삭제 성공 - 허브 매니저가 본인 허브 상품 삭제")
	void deleteProduct_success() {
		// given
		ProductDeleteRequestServiceDto requestDto = new ProductDeleteRequestServiceDto(PRODUCT_ID);

		HubSearchResponseDto hub = new HubSearchResponseDto(
			COMPANY_MANAGE_HUB_ID, HUB_NAME, HUB_ADDRESS, HUB_LATITUDE, HUB_LONGITUDE, HUB_MANAGER_ID
		);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(HUB_MANAGER_ID)).thenReturn(Optional.of(hub));

		// when
		assertDoesNotThrow(() -> productService.deleteProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER));

		// then
		assertDoesNotThrow(() -> productService.deleteProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER));
	}

	@Test
	@Description("상품 삭제 실패 - 상품이 존재하지 않음")
	void deleteProduct_fail_notFound() {
		// given
		ProductDeleteRequestServiceDto requestDto = new ProductDeleteRequestServiceDto(PRODUCT_ID);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.empty());

		// when & then
		ProductException exception = assertThrows(ProductException.class, () ->
			productService.deleteProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
	}

	@Test
	@Description("상품 삭제 실패 - 다른 허브의 상품")
	void deleteProduct_fail_wrongHub() {
		// given
		ProductDeleteRequestServiceDto requestDto = new ProductDeleteRequestServiceDto(PRODUCT_ID);

		UUID otherHubId = UUID.randomUUID();
		HubSearchResponseDto hub = new HubSearchResponseDto(
			otherHubId, OTHER_HUB_NAME, OTHER_HUB_ADDRESS, OTHER_HUB_LATITUDE, OTHER_HUB_LONGITUDE, HUB_MANAGER_ID
		);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(HUB_MANAGER_ID)).thenReturn(Optional.of(hub));

		// when & then
		ProductException exception = assertThrows(ProductException.class, () ->
			productService.deleteProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION);
	}

	@Test
	@Description("상품 삭제 실패 - 허브 정보 없음")
	void deleteProduct_fail_hubNotFound() {
		// given
		ProductDeleteRequestServiceDto requestDto = new ProductDeleteRequestServiceDto(PRODUCT_ID);

		when(productRepository.findByIdAndIsDeletedFalse(PRODUCT_ID)).thenReturn(Optional.of(product));
		when(hubClient.isManagerOfHub(HUB_MANAGER_ID)).thenReturn(Optional.empty());

		// when & then
		ProductException exception = assertThrows(ProductException.class, () ->
			productService.deleteProduct(requestDto, HUB_MANAGER_ID, UserRoleType.HUB_MANAGER)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HUB_NOT_FOUND);
	}


}