package com.oingmaryho.business.productservice.application.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oingmaryho.business.productservice.application.dto.request.ProductQueueRequestDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductQueueResponseDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.productservice.application.dto.request.ProductQueueFailedRequestDto;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;
import com.oingmaryho.business.productservice.exception.ErrorCode;
import com.oingmaryho.business.productservice.exception.ProductException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCompensationListener {

	private final ProductRepository productRepository;

	@RabbitListener(queues = "queueErrProduct")
	@Transactional
	public void handleOrderFailure(String event) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ProductQueueRequestDto requestDto = objectMapper.readValue(event, ProductQueueRequestDto.class);
		Product product = productRepository.findByIdAndIsDeletedFalse(requestDto.productId())
				.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));

		product.increaseStock(requestDto.quantity());
	}
}