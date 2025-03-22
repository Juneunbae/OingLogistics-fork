package com.oingmaryho.business.hubservice.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubInfoService {

	private final HubRepository hubRepository;
	private final GeoSpatialService geoSpatialService;

	public Hub getNearestHubFromAddress(String address) {
		Address geoAddress = geoSpatialService.getGeoSpatial(address);
		List<Hub> hubs = hubRepository.findAllActiveHubs();
		return getNearestHub(geoAddress, hubs);
	}

	private Hub getNearestHub(Address geoAddress, List<Hub> hubs) {
		double latitude = geoAddress.getLatitude();
		double longitude = geoAddress.getLongitude();

		return hubs.stream()
			.min((h1, h2) -> {
				double distance1 = getDistance(latitude, longitude, h1.getAddress().getLatitude(), h1.getAddress().getLongitude());
				double distance2 = getDistance(latitude, longitude, h2.getAddress().getLatitude(), h2.getAddress().getLongitude());
				return Double.compare(distance1, distance2);
			})
			.orElseThrow(() -> new HubException(ErrorCode.NOT_FOUND_HUB));
	}

	private double getDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
		return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2));
	}
}
