package com.oingmaryho.business.productservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.productservice.application.service.ProductService;
import com.oingmaryho.business.productservice.application.dto.request.ProductCreateRequestServiceDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductCreateResponseServiceDto;
import com.oingmaryho.business.productservice.presentation.dto.request.ProductCreateRequestDto;
import com.oingmaryho.business.productservice.presentation.dto.response.ProductCreateResponseDto;
import com.oingmaryho.business.productservice.presentation.mapper.ProductPresentationMapper;

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
}
