package com.oingmaryho.business.hubservice.infrastructure.map.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionsResponse {
	private Route route;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Route {
		private List<Traoptimal> traoptimal;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Traoptimal {
		private Summary summary;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Summary {
		private Integer distance;
		private Integer duration;
	}
}
