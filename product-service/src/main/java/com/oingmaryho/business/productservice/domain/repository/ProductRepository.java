package com.oingmaryho.business.productservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.oingmaryho.business.productservice.domain.Product;

@Repository
public interface ProductRepository {
	Optional<Product> findByIdAndIsDeletedFalse(UUID id);
	boolean existsByProductCodeAndIsDeletedFalse(String productCode);

	Product save(Product product);
}
