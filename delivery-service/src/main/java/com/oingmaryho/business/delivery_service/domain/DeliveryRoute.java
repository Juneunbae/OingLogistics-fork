package com.oingmaryho.business.delivery_service.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_delivery_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryRoute extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID deliveryId;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private UUID departureHubId;

    @Column(nullable = false)
    private UUID destinationHubId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryRouteStatus status = DeliveryRouteStatus.HUB_WAITING;

    @Column(nullable = false)
    private Double estimatedDistance;

    @Column(nullable = false)
    private Integer estimatedTime;

    private Double actualDistance;
    private Integer actualTime;

    @Column(nullable = false)
    private Long managerId;

    /**
     * 배송 상태 변경
     * @param newStatus 변경할 상태
     */
    public void changeStatus(DeliveryRouteStatus newStatus){
        this.status = newStatus;
    }

    /**
     * 배송 실제 거리, 실제 소요시간 입력
     * @param actualDistance 실제 거리
     * @param actualTime 실제 소요시간
     */
    public void updateActualDistanceAndTime(double actualDistance, int actualTime) {
        this.actualDistance = actualDistance;
        this.actualTime = actualTime;
    }
}
