package com.oingmaryho.business.productservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.productservice.domain.Product;

public interface CustomProductRepository {
	Page<Product> findDynamicQuery(ProductSearchCriteria searchCriteria, Pageable pageable);
}
