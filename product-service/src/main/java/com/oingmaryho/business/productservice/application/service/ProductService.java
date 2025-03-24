package com.oingmaryho.business.productservice.application.service;


import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.productservice.application.dto.request.ProductDeleteRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductDetailsSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductSearchRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.request.ProductUpdateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductDetailsSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductUpdateResponseServiceDto;
import com.oingmaryho.business.productservice.application.mapper.ProductApplicationMapper;
import com.oingmaryho.business.productservice.application.service.feignClient.CompanyClient;
import com.oingmaryho.business.productservice.application.service.feignClient.HubClient;
import com.oingmaryho.business.productservice.config.cache.CacheType;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.ProductSearchCriteria;
import com.oingmaryho.business.productservice.domain.repository.CustomProductRepository;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;
import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.exception.ErrorCode;
import com.oingmaryho.business.productservice.exception.ProductException;
import com.oingmaryho.business.productservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.productservice.presentation.dto.response.HubSearchResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final HubClient hubClient;
	private final CompanyClient companyClient;
	private final ProductRepository productRepository;
	private final CustomProductRepository customProductRepository;
	private final ProductApplicationMapper productApplicationMapper;

	@Transactional
	public ProductCreateResponseServiceDto createProduct(
		ProductCreateRequestServiceDto productCreateRequestServiceDto,
		Long userId,
		UserRoleType role
	){
		UUID checkTargetId = (role == UserRoleType.HUB_MANAGER) ? productCreateRequestServiceDto.manageHubId() : productCreateRequestServiceDto.companyId();
		CompanyDetailsSearchResponseDto company = companyClient(productCreateRequestServiceDto.companyId());
		HubSearchResponseDto hub = hubClient(productCreateRequestServiceDto.manageHubId());
		validateManagerPermission(checkTargetId, userId, role);
		validateSupplierCompany(company);



		String productCode = productCreateRequestServiceDto.productCode();
		if (productRepository.existsByProductCodeAndIsDeletedFalse(productCode)) {
			throw new ProductException(ErrorCode.ALREADY_REGISTERED_PRODUCT);
		}

		Product product = productApplicationMapper.toCreateEntity(productCreateRequestServiceDto,company.name(),hub.id());
		Product saveProduct = productRepository.save(product);
		return new ProductCreateResponseServiceDto(saveProduct.getId());
	}

	public Page<ProductSearchResponseServiceDto> searchProducts(
		ProductSearchRequestServiceDto requestDto,
		Pageable pageable,
		Long userId,
		UserRoleType role
	){
		if (role == UserRoleType.HUB_MANAGER) {
			UUID myHubId = hubClient.isManagerOfHub(userId)
				.map(HubSearchResponseDto::id)
				.orElseThrow(() -> new ProductException(ErrorCode.HUB_NOT_FOUND));

			requestDto = new ProductSearchRequestServiceDto(
				requestDto.id(),
				requestDto.productCode(),
				requestDto.name(),
				myHubId,
				requestDto.companyId(),
				requestDto.companyName(),
				requestDto.minPrice(),
				requestDto.maxPrice(),
				requestDto.minStock(),
				requestDto.maxStock(),
				requestDto.isDeleted()
			);
		}
		validateSearchCriteria(createProductSearchCriteria(requestDto));
		Page<Product> products = customProductRepository.findDynamicQuery(createProductSearchCriteria(requestDto),pageable);
		return products.map(productApplicationMapper::toProductSearchResponseServiceDto);
	}

	@Cacheable(
		cacheNames = CacheType.PRODUCT_CACHE,
		key = "'productDetails:' + #requestDto.id() + ':' + #role.name() + ':' + #userId"
	)
	public ProductDetailsSearchResponseServiceDto getProductDetails(
		ProductDetailsSearchRequestServiceDto requestDto,
		Long userId,
		UserRoleType role
	) {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));
		if (role == UserRoleType.HUB_MANAGER) {
			UUID myHubId = hubClient.isManagerOfHub(userId)
				.orElseThrow(() -> new ProductException(ErrorCode.HUB_NOT_FOUND)).id();

			if (!product.getManageHubId().equals(myHubId)) {
				throw new ProductException(ErrorCode.NO_PERMISSION);
			}
		}
		return productApplicationMapper.toResponseDto(product);
	}

	@CacheEvict(
		cacheNames = CacheType.PRODUCT_CACHE,
		key = "'productDetails:' + #requestServiceDto.id() + ':' + #role.name() + ':' + #userId"
	)
	@Transactional
	public ProductUpdateResponseServiceDto updateProduct(
		ProductUpdateRequestServiceDto requestServiceDto,
		Long userId,
		UserRoleType role
	) {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));

		UUID checkTargetId = (role == UserRoleType.HUB_MANAGER) ?  product.getManageHubId() :product.getCompanyId();
		validateManagerPermission(checkTargetId, userId, role);

		HubSearchResponseDto hubSearchResponseDto = hubClient(requestServiceDto.manageHubId());

		product.update(
			hubSearchResponseDto.id(),
			requestServiceDto.name(),
			requestServiceDto.price(),
			requestServiceDto.stock()
		);
		return productApplicationMapper.toUpdateResponseDto(product.getId());
	}

	@CacheEvict(
		cacheNames = CacheType.PRODUCT_CACHE,
		key = "'productDetails:' + #requestServiceDto.id() + ':' + #role.name() + ':' + #userId"
	)
	@Transactional
	public void deleteProduct(
		ProductDeleteRequestServiceDto requestServiceDto,
		Long userId,
		UserRoleType role) {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
			.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));

		validateManagerPermission(product.getManageHubId(), userId, role);

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
			.isDeleted(requestDto.isDeleted())
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

	private void validateManagerPermission(UUID manageId, Long userId, UserRoleType role) {
		if (role == UserRoleType.HUB_MANAGER) {
			HubSearchResponseDto managedHub = hubClient.isManagerOfHub(userId)
				.orElseThrow(() -> new ProductException(ErrorCode.HUB_NOT_FOUND));

			if (!manageId.equals(managedHub.id())) {
				throw new ProductException(ErrorCode.NO_PERMISSION);
			}
		} else if (role == UserRoleType.COMPANY_MANAGER) {
			CompanyDetailsSearchResponseDto company = companyClient.getCompanyById(manageId)
				.orElseThrow(() -> new ProductException(ErrorCode.COMPANY_NOT_FOUND));

			if (!company.managerId().equals(userId)) {
				throw new ProductException(ErrorCode.NO_PERMISSION);
			}
		}
	}

	private HubSearchResponseDto hubClient(UUID manageHubId) {
		return hubClient.getHubById(manageHubId)
			.orElseThrow(() -> new ProductException(ErrorCode.HUB_NOT_FOUND));
	}

	private CompanyDetailsSearchResponseDto companyClient(UUID companyId) {
		return companyClient.getCompanyById(companyId)
			.orElseThrow(() -> new ProductException(ErrorCode.COMPANY_NOT_FOUND));

	}

	private void validateSupplierCompany(CompanyDetailsSearchResponseDto company) {

		if (!company.type().equals("SUPPLIER")) {
			throw new ProductException(ErrorCode.INVALID_COMPANY_TYPE);
		}
	}
}
