package com.oingmaryho.business.productservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.infrastructure.ProductJpaRepository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

	private final ProductJpaRepository productJpaRepository;

	public ProductRepositoryImpl(ProductJpaRepository productJpaRepository) {
		this.productJpaRepository = productJpaRepository;
	}

	@Override
	public Optional<Product> findByIdAndIsDeletedFalse(UUID id) {
		return productJpaRepository.findByIdAndIsDeletedFalse(id);
	}
}