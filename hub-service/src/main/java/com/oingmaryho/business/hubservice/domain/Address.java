package com.oingmaryho.business.hubservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Address {

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;
}
