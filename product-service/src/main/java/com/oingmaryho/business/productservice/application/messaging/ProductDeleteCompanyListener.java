package com.oingmaryho.business.productservice.application.messaging;

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;
import com.oingmaryho.business.productservice.application.dto.request.CompanyProductDeleteRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductDeleteCompanyListener {
	private final ProductRepository productRepository;

	@Transactional
	@RabbitListener(queues = "queueCompanyDelete")
	public void handleCompanyDeleted(CompanyProductDeleteRequestDto event) {
		List<Product> products = productRepository.findAllByCompanyIdAndIsDeletedFalse(event.companyId());

		products.forEach(product -> product.softDelete(event.userId()));
	}
}
