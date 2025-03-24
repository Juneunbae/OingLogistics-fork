package com.oingmaryho.business.orderservice.domain;

import com.oingmaryho.business.orderservice.application.dto.request.OrderUpdateRequestServiceDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
        nullable = false
    )
    private UUID requesterId;

    @Column(
        nullable = false,
        length = 50
    )
    private String requesterName;

    @Column(
        nullable = false,
        length = 100
    )
    private String requesterAddress;

    private UUID requesterHubId;

    private Long requesterUserId;

    @Column(
        length = 50
    )
    private String requesterUsername;

    @Column(
        nullable = false,
        length = 50
    )
    private String requesterSlackId;

    @Column(
        nullable = false
    )
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(
        nullable = false
    )
    private Integer totalPrice;

    @Column(
        length = 100
    )
    private String requests;

    @Column(
        nullable = false,
        columnDefinition = "boolean default false"
    )
    private Boolean isDeleted;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void update(OrderUpdateRequestServiceDto orderUpdateRequestServiceDto) {
        if (orderUpdateRequestServiceDto.requests() != null) {
            this.requests = orderUpdateRequestServiceDto.requests();
        }

        if (orderUpdateRequestServiceDto.totalPrice() != null) {
            this.totalPrice = orderUpdateRequestServiceDto.totalPrice();
        }
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void updateTotalPrice(Integer totalPrice) {
        this.totalPrice -= totalPrice;
    }

    public void inputTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void addOrderDetail(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void successOrder(Status status) {
        this.status = status;
    }
}