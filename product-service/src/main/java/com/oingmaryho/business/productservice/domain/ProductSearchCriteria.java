package com.oingmaryho.business.productservice.domain;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSearchCriteria {
	private final UUID id;
	private final String name;
	private final UUID manageHunId;

	private final Long minPrice;
	private final Long maxPrice;

	private final Long minStock;
	private final Long maxStock;

	private final Boolean isDeleted;
}
