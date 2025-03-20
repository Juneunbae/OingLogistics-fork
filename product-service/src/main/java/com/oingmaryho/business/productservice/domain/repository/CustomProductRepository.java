package com.oingmaryho.business.productservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.ProductSearchCriteria;

@Repository
public interface CustomProductRepository {
	Page<Product> findDynamicQuery(ProductSearchCriteria searchCriteria, Pageable pageable);
}
