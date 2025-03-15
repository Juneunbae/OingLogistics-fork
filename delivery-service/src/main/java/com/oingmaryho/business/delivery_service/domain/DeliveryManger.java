package com.oingmaryho.business.delivery_service.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_manager")
public class DeliveryManger extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String slackId;
    private UUID hubId;
    private Long managerId;
    private DeliveryManagerType type;
    private Integer sequence;
}
