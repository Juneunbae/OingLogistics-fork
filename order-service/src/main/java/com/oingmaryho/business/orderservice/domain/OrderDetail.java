package com.oingmaryho.business.orderservice.domain;

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
@Table(name = "p_order_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(
        nullable = false
    )
    private UUID requesterId;

    @Column(
        length = 30
    )
    private String requesterName;

    @Column(
        nullable = false
    )
    private UUID shippingId;

    @Column(
        nullable = false
    )
    private UUID productId;

    private String productName;

    @Column(
        nullable = false
    )
    private Integer quantity;

    @Column(
        nullable = false
    )
    private Integer price;
}