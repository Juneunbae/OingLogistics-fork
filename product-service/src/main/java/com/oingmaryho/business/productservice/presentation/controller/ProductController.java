package com.oingmaryho.business.productservice.presentation.controller;

import java.util.UUID;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.productservice.application.dto.response.ProductSearchResponseServiceDto;
import com.oingmaryho.business.productservice.application.service.ProductService;
import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.presentation.dto.request.ProductCreateRequestDto;
import com.oingmaryho.business.productservice.presentation.dto.request.ProductSearchRequestDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductCreateResponseDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductSearchResponseDto;
import com.oingmaryho.business.productservice.presentation.mapper.ProductPresentationMapper;
import com.oingmaryho.business.productservice.utils.PageableUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

	private final ProductService productService;
	private final ProductPresentationMapper productPresentationMapper;

	@Description("일반 - 상품 등록")
	@PostMapping
	public ResponseEntity<ProductCreateResponseDto> createProduct(@RequestBody ProductCreateRequestDto productCreateRequestDto){
		// TODO: userId, role 받아오기
		ProductCreateRequestServiceDto requestServiceDto = productPresentationMapper.toCreateServiceDto(productCreateRequestDto);
		ProductCreateResponseServiceDto responseServiceDto = productService.createProduct(requestServiceDto);
		ProductCreateResponseDto responseDto = productPresentationMapper.toCreateDto(responseServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("일반 - 상품 전체 조회")
	@GetMapping
	public ResponseEntity<Page<ProductSearchResponseDto>> getProducts(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "10") int size,
		@RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
		@RequestParam(name = "by", defaultValue = "name") String by,
		@RequestParam(value = "id", required = false) UUID id,
		@RequestParam(name = "productCode", required = false) String productCode,
		@RequestParam(name = "name", required = false) String name,
		@RequestParam(name = "manageHubId", required = false) UUID manageHubId,
		@RequestParam(name = "companyId", required = false) UUID companyId,
		@RequestParam(name = "minPrice", required = false) Long minPrice,
		@RequestParam(name = "maxPrice", required = false) Long maxPrice,
		@RequestParam(name = "minStock", required = false) Long minStock,
		@RequestParam(name = "maxStock", required = false) Long maxStock
	){
		// TODO: userId, role 받아오기
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		ProductSearchRequestDto requestDto = new ProductSearchRequestDto(id, productCode, name, manageHubId, companyId, minPrice, maxPrice, minStock,maxStock);

		Page<ProductSearchResponseServiceDto> responseDto = productService.searchProducts(productPresentationMapper.toProductSearchResponseServiceDto(requestDto), pageable);

		return ResponseEntity.ok(responseDto.map(productPresentationMapper::toProductSearchResponseDto));
	}


}
