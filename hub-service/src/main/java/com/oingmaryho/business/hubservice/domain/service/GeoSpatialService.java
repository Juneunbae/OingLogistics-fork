package com.oingmaryho.business.hubservice.domain.service;

import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.RouteInfo;

public interface GeoSpatialService {

	Address getGeoSpatial(String address);

	RouteInfo getRouteInfo(Address from, Address to);
}
