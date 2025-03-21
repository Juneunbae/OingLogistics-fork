package com.oingmaryho.business.hubservice.domain.service;

import com.oingmaryho.business.hubservice.domain.Address;

public interface GeoSpatialService {

	Address getGeoSpatial(String address);
}
