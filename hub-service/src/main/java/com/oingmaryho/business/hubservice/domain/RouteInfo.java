package com.oingmaryho.business.hubservice.domain;

import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class RouteInfo {

	@Column(nullable = false)
	private Integer hubToHubTime;

	@Column(nullable = false)
	private Double distance;

	public RouteInfo(Integer hubToHubTime, Double distance) {
		validateHubToHubTime(hubToHubTime);
		validateDistance(distance);

		this.hubToHubTime = hubToHubTime;
		this.distance = distance;
	}

	private void validateHubToHubTime(Integer hubToHubTime) {
		if (hubToHubTime == null || hubToHubTime < 0) {
			throw new HubException(ErrorCode.NOT_VALID_TIME);
		}
	}

	private void validateDistance(Double distance) {
		if (distance == null || distance < 0) {
			throw new HubException(ErrorCode.NOT_VALID_DISTANCE);
		}
	}
}
