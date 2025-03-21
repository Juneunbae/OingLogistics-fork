package com.oingmaryho.business.hubservice.domain.service;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.Hub;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubCreateService {

	private final GeoSpatialService geoSpatialService;

	public Hub createHub(String name, String address, Long managerId) {
		Address geoSpatialAddress = geoSpatialService.getGeoSpatial(address);
		return Hub.builder()
			.name(name)
			.address(geoSpatialAddress)
			.managerId(managerId)
			.build();
	}
}
