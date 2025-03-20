package com.oingmaryho.business.hubservice.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_hub_route")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubRoute extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	// TODO : 관계 매핑 질문하기
	@Column(nullable = false)
	private UUID departureHubId;

	@Column(nullable = false)
	private UUID arriveHubId;

	@Embedded
	private RouteInfo routeInfo;

	@Column(nullable = false)
	@Builder.Default
	private Boolean isDeleted = false;
}
