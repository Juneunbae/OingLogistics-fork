package com.oingmaryho.business.companyservice.domain;

import lombok.Getter;

@Getter
public enum CompanyType {
	RECEIVER("수령 업체"),
	SUPPLIER("공급 업체");

	private final String description;
	CompanyType(String description) {
		this.description = description;
	}

}
