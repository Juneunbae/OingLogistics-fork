package com.oingmaryho.business.productservice.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.repository.ProductRepository;

public interface ProductJpaRepository extends JpaRepository<Product, UUID>, ProductRepository {
}
