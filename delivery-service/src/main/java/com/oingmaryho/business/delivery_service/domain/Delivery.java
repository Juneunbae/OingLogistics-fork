package com.oingmaryho.business.delivery_service.domain;

import com.oingmaryho.business.delivery_service.application.dto.request.DeliveryUpdateRequestServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.request.DeliveryUpdateStatusRequestServiceDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
import java.util.UUID;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_delivery")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;      // TODO 배송 생성 시 주문 id 유효성 검사

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.HUB_WAITING;

    @Column(nullable = false)
    private UUID departureHubId;

    @Column(nullable = false)
    private UUID destinationHubId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private String receiverSlackId;

    @Column(nullable = false)
    private UUID managerId; // 업체 배송 담당자 userId

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryRoute> routes;

    public void update(DeliveryUpdateRequestServiceDto requestServiceDto) {
        if (receiver != null) {
            this.receiver = requestServiceDto.receiver();
        }
        if (receiverSlackId != null) {
            this.receiverSlackId = requestServiceDto.receiverSlackId();
        }
        if (address != null) {
            this.address = requestServiceDto.address();
        }
        if (managerId != null) {
            this.managerId = requestServiceDto.managerId();
        }
    }

    public void updateStatus(DeliveryUpdateStatusRequestServiceDto requestServiceDto) {
        this.status = requestServiceDto.status();
    }

}
