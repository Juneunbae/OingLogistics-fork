package com.oingmaryho.business.delivery_service.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_delivery")
public class Delivery extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID orderId;
    private DeliveryStatus status;
    private UUID departureHubId;
    private UUID destinationHubId;
    private String address;
    private String receiver;
    private String receiverSlackId;
    private Long managerId;
}
