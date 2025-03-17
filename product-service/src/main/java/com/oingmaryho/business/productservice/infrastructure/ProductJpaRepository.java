package com.oingmaryho.business.productservice.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.oingmaryho.business.productservice.domain.Product;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
	Optional<Product> findByIdAndIsDeletedFalse(UUID id);
}
