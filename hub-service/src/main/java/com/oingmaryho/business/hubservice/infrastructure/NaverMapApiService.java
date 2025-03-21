package com.oingmaryho.business.hubservice.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.service.GeoSpatialService;

@Service
public class NaverMapApiService implements GeoSpatialService {

	private final String geocodingUrl;

	private final HttpHeaders headers;
	private final RestTemplate restTemplate;

	private NaverMapApiService(
		@Value("${map.naver.client.id}") String clientId,
		@Value("${map.naver.client.secret}") String clientSecret,
		@Value("${map.naver.service.geocoding.url}") String geocodingUrl
	) {
		this.geocodingUrl = geocodingUrl;

		this.headers = new HttpHeaders();
		headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
		headers.add("X-NCP-APIGW-API-KEY", clientSecret);
		headers.add("Accept", "application/json");

		this.restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	}

	@Override
	public Address getGeoSpatial(String address) {
		try {
			String url = geocodingUrl + "?query=" + address;

			ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				new HttpEntity<>(headers),
				String.class
			);

			String body = response.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(body);

			Double longitude = root.get("addresses").get(0).get("x").asDouble();
			Double latitude = root.get("addresses").get(0).get("y").asDouble();

			return new Address(address, latitude, longitude);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get geospatial information from Naver Map API", e);
		}
	}
}
