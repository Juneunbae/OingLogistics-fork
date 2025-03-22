package com.oingmaryho.business.productservice.infrastructure.messaging;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.productservice.application.dto.request.ProductQueueRequestDto;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductOrderListener {

	private final ProductRepository productRepository;

	@RabbitListener(queues = "queueProduct")
	@Transactional
	public String handleQueueProduct(String event) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ProductQueueRequestDto requestDto = objectMapper.readValue(event, ProductQueueRequestDto.class);

		log.info("Received message: {}", requestDto);

		Optional<Product> optional = productRepository.findByIdAndIsDeletedFalse(requestDto.productId());
		if (optional.isEmpty()) {
			log.warn("Product not found: {}", requestDto.productId());
			return objectMapper.writeValueAsString(Integer.toString(HttpStatus.NOT_FOUND.value()));
		}

		Product product = optional.get();

		if (product.getStock() < requestDto.quantity()) {
			log.warn("Insufficient stock: product={}, requested={}", product.getStock(), requestDto.quantity());
			return objectMapper.writeValueAsString(Integer.toString(HttpStatus.BAD_REQUEST.value()));
		}

		product.decreaseStock(requestDto.quantity());

		if (product.getStock() <= 100) {
			int replenishAmount = 1000 - product.getStock();
			product.increaseStock(replenishAmount);
			log.info("Replenished stock: product={}, amount={}", product.getId(), replenishAmount);
		}

		log.info("Message processed successfully: {}", requestDto);
		return objectMapper.writeValueAsString(Integer.toString(HttpStatus.OK.value()));
	}
}