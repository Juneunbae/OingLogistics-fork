package com.oingmaryho.business.delivery_service.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_route")
public class DeliveryRoute extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID deliveryId;
    private Integer sequence;
    private UUID departureHubId;
    private UUID destinationHubId;
    private DeliveryRouteStatus status;
    private Double estimatedDistance;
    private Integer estimatedTime;
    private Double actualDistance;
    private Integer actualTime;
    private Long managerId;
}
