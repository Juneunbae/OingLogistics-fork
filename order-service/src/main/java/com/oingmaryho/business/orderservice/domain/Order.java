package com.oingmaryho.business.orderservice.domain;

import com.oingmaryho.business.orderservice.application.dto.OrderUpdateDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Table(name = "p_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
        nullable = false
    )
    private UUID recipientId;

    @Column(
        nullable = false,
        length = 50
    )
    private String recipientName;

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
        nullable = false
    )
    private Boolean isDeleted;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

    public void update(OrderUpdateDto orderUpdateDto) {
        if (orderUpdateDto.requests() != null) {
            this.requests = orderUpdateDto.requests();
        }

        if (orderUpdateDto.totalPrice() != null) {
            this.totalPrice = orderUpdateDto.totalPrice();
        }
    }

    public void delete() {
        this.isDeleted = true;
    }
}