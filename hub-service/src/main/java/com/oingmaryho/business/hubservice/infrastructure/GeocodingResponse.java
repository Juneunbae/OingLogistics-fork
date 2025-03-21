package com.oingmaryho.business.hubservice.infrastructure;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponse {

	private List<AddressItem> addresses;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AddressItem {
		private String x;
		private String y;
	}
}
