package com.oingmaryho.business.delivery_service.domain.entity;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Builder
@Table(name = "p_delivery_manager")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryManager extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String slackId;

    private UUID hubId; // 허브 배송 담당자, 업체 배송 담당자 -> 소속 허브

    private UUID companyId; // 업체 배송 담당자 -> 소속 업체

    @Column(nullable = false)
    private Long managerId; // 실제 user-service에서 조회할 때 사용할 id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryManagerType type;   // TODO 배송 담당자 타입이 업체 배송담당자인 경우, 소속 허브 ID 유효성 검사

    @Column(nullable = false)
    private Integer sequence;
}
