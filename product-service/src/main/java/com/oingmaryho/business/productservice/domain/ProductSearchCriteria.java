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
	private final String productCode;
	private final String name;
	private final UUID manageHubId;
	private final UUID companyID;
	private final String companyName;

	private final Integer minPrice;
	private final Integer maxPrice;

	private final Integer minStock;
	private final Integer maxStock;

	private final Boolean isDeleted;
}
