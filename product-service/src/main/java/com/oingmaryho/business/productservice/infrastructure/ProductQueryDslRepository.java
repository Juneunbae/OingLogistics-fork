package com.oingmaryho.business.productservice.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.oingmaryho.business.productservice.domain.Product;
import com.oingmaryho.business.productservice.domain.ProductSearchCriteria;
import com.oingmaryho.business.productservice.domain.QProduct;
import com.oingmaryho.business.productservice.domain.repository.CustomProductRepository;
import com.oingmaryho.business.productservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class ProductQueryDslRepository implements CustomProductRepository {

	private final JPAQueryFactory queryFactory;

	public ProductQueryDslRepository(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public Page<Product> findDynamicQuery(ProductSearchCriteria searchCriteria, Pageable pageable){
		QProduct product = QProduct.product;
		BooleanBuilder builder =buildSearchConditions(searchCriteria, product);

			List<Product> result = queryFactory
			.selectFrom(product)
			.where(builder)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Product.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.selectFrom(product)
			.where(builder)
			.fetchCount();

		return PageableExecutionUtils.getPage(result, pageable, () -> total);
	}

	private BooleanBuilder buildSearchConditions(ProductSearchCriteria searchCriteria, QProduct product) {
		BooleanBuilder builder = new BooleanBuilder();

		addIdCondition(builder, searchCriteria.getId(), product);
		addProductCodeCondition(builder, searchCriteria.getProductCode(), product);
		addNameCondition(builder, searchCriteria.getName(), product);
		addManageHubIdCondition(builder, searchCriteria.getManageHubId(), product);
		addCompanyNameCondition(builder, searchCriteria.getCompanyName(), product);
		addCompanyIdCondition(builder, searchCriteria.getCompanyID(), product);
		addPriceConditions(builder, searchCriteria.getMinPrice(), searchCriteria.getMaxPrice(), product);
		addStockConditions(builder, searchCriteria.getMinStock(), searchCriteria.getMaxStock(), product);
		addIsDeletedCondition(builder, searchCriteria.getIsDeleted(), product);

		return builder;
	}

	private void addIdCondition(BooleanBuilder builder, UUID id, QProduct product) {
		if (id != null) {
			builder.and(product.id.eq(id));
		}
	}

	private void addProductCodeCondition(BooleanBuilder builder, String productCode, QProduct product) {
		if (productCode != null) {
			builder.and(product.productCode.eq(productCode));
		}
	}

	private void addNameCondition(BooleanBuilder builder, String name, QProduct product) {
		if (name != null) {
			builder.and(product.name.containsIgnoreCase(name));
		}
	}

	private void addManageHubIdCondition(BooleanBuilder builder, UUID manageHubId, QProduct product) {
		if (manageHubId != null) {
			builder.and(product.manageHubId.eq(manageHubId));
		}
	}

	private void addCompanyIdCondition(BooleanBuilder builder, UUID companyId, QProduct product) {
		if (companyId != null) {
			builder.and(product.companyId.eq(companyId));
		}
	}

	private void addCompanyNameCondition(BooleanBuilder builder, String companyName, QProduct product) {
		if (companyName != null) {
			builder.and(product.companyName.eq(companyName));
		}
	}

	private void addPriceConditions(BooleanBuilder builder, Long minPrice, Long maxPrice, QProduct product) {
		if (minPrice != null) {
			builder.and(product.price.goe(minPrice));
		}
		if (maxPrice != null) {
			builder.and(product.price.loe(maxPrice));
		}
	}

	private void addStockConditions(BooleanBuilder builder, Long minStock, Long maxStock, QProduct product) {
		if (minStock != null) {
			builder.and(product.stock.goe(minStock));
		}
		if (maxStock != null) {
			builder.and(product.stock.loe(maxStock));
		}
	}

	private void addIsDeletedCondition(BooleanBuilder builder, Boolean isDeleted, QProduct product) {
		if (isDeleted != null) {
			builder.and(product.isDeleted.eq(isDeleted));
		}
	}
}