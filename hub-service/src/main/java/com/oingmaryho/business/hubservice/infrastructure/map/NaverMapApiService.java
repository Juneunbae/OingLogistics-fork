package com.oingmaryho.business.hubservice.infrastructure.map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.RouteInfo;
import com.oingmaryho.business.hubservice.domain.service.GeoSpatialService;
import com.oingmaryho.business.hubservice.infrastructure.map.dto.DirectionsResponse;
import com.oingmaryho.business.hubservice.infrastructure.map.dto.GeocodingResponse;

@Service
public class NaverMapApiService implements GeoSpatialService {

	private final String geocodingUrl;
	private final String directions5Url;
	private final WebClient webClient;

	private NaverMapApiService(
		@Value("${map.naver.client.id}") String clientId,
		@Value("${map.naver.client.secret}") String clientSecret,
		@Value("${map.naver.service.geocoding.url}") String geocodingUrl,
		@Value("${map.naver.service.directions5.url}") String directions5Url
	) {
		this.geocodingUrl = geocodingUrl;
		this.directions5Url = directions5Url;

		this.webClient = WebClient.builder()
			.defaultHeader("X-NCP-APIGW-API-KEY-ID", clientId)
			.defaultHeader("X-NCP-APIGW-API-KEY", clientSecret)
			.defaultHeader("Accept", "application/json")
			.build();
	}

	@Override
	public Address getGeoSpatial(String address) {
		try {
			String url = geocodingUrl + "?query=" + address;

			GeocodingResponse response = webClient.get()
				.uri(url)
				.retrieve()
				.bodyToMono(GeocodingResponse.class)
				.block();

			if (response == null || response.getAddresses().isEmpty()) {
				throw new RuntimeException("No address found for : " + address);
			}

			GeocodingResponse.AddressItem addressItem = response.getAddresses().get(0);

			Double latitude = Double.parseDouble(addressItem.getY());
			Double longitude = Double.parseDouble(addressItem.getX());

			return new Address(address, latitude, longitude);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get geospatial information from Naver Map API", e);
		}
	}

	@Override
	public RouteInfo getRouteInfo(Address from, Address to) {
		try {
			String url = directions5Url +
				"?start=" + from.getLongitude() + "," + from.getLatitude() +
				"&goal=" + to.getLongitude() + "," + to.getLatitude();

			DirectionsResponse response = webClient.get()
				.uri(url)
				.retrieve()
				.bodyToMono(DirectionsResponse.class)
				.block();

			if (response == null || response.getRoute() == null) {
				throw new RuntimeException("No route found for : " + from + " -> " + to);
			}

			DirectionsResponse.Summary summary = response.getRoute().getTraoptimal().get(0).getSummary();

			Integer hubToHubTime = summary.getDuration();
			hubToHubTime = (hubToHubTime / 1000) / 60;

			Double distance = Double.valueOf(summary.getDistance());
			distance = distance / 1000;

			return new RouteInfo(hubToHubTime, distance);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get route information from Naver Map API", e);
		}
	}
}
