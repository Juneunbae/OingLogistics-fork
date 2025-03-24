package com.oingmaryho.business.productservice.application.messaging;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.productservice.application.dto.request.ProductQueueRequestDto;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;
import com.oingmaryho.business.productservice.exception.ErrorCode;
import com.oingmaryho.business.productservice.exception.ProductException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCompensationListener {

	private final ProductRepository productRepository;

	@RabbitListener(queues = "queueErrProduct")
	@Transactional
	public void handleOrderFailure(ProductQueueRequestDto requestDto) throws IOException {
		Product product = productRepository.findByIdAndIsDeletedFalse(requestDto.productId())
				.orElseThrow(() -> new ProductException(ErrorCode.NOT_FOUND));

		product.increaseStock(requestDto.quantity());
	}
}