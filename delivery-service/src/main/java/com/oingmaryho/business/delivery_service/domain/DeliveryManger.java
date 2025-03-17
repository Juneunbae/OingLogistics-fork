package com.oingmaryho.business.delivery_service.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;


@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_delivery_manager")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class DeliveryManger extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String slackId;

    private UUID hubId;

    @Column(nullable = false)
    private Long managerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryManagerType type;   // TODO 배송 담당자 타입이 업체 배송담당자인 경우, 소속 허브 ID 유효성 검사

    @Column(nullable = false)
    private Integer sequence;
}
