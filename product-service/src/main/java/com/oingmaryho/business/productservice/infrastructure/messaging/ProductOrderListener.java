package com.oingmaryho.business.productservice.infrastructure.messaging;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.productservice.application.dto.request.ProductQueueRequestDto;
import com.oingmaryho.business.productservice.application.dto.response.ProductQueueResponseDto;
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
	public ProductQueueResponseDto handleQueueProduct(ProductQueueRequestDto event) {

		Optional<Product> optional = productRepository.findByIdAndIsDeletedFalse(event.productId());
		if (optional.isEmpty()) {
			return new ProductQueueResponseDto(404);
		}

		Product product = optional.get();

		if (product.getStock() < event.quantity()) {
			return new ProductQueueResponseDto(400);
		}

		product.decreaseStock(event.quantity());

		if (product.getStock() <= 100) {
			int replenishAmount = 1000 - product.getStock();
			product.increaseStock(replenishAmount);
		}

		return new ProductQueueResponseDto(200);
	}
}