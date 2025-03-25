package com.oingmaryho.business.orderservice.infrastructure;


import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderSearchCriteria;
import com.oingmaryho.business.orderservice.domain.repository.CustomOrderRepository;
import com.oingmaryho.business.orderservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.oingmaryho.business.orderservice.domain.QOrder.order;
import static com.oingmaryho.business.orderservice.domain.QOrderDetail.orderDetail;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository implements CustomOrderRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findDynamicQuery(OrderSearchCriteria criteria, Pageable pageable) {
        BooleanBuilder conditions = new BooleanBuilder(); // 조건을 동적으로 추가할 수 있음

        if (criteria.getIsDeleted() != null) {
            conditions.and(order.isDeleted.eq(criteria.getIsDeleted()));
        }

        if (criteria.getProductName() != null && !criteria.getProductName().isEmpty()) {
            conditions.and(orderDetail.productName.likeIgnoreCase("%" + criteria.getProductName() + "%"));
        }

        if (criteria.getRecipientName() != null && !criteria.getRecipientName().isEmpty()) {
            conditions.and(orderDetail.recipientName.likeIgnoreCase("%" + criteria.getRecipientName() + "%"));
        }

        if (criteria.getRequesterName() != null && !criteria.getRequesterName().isEmpty()) {
            conditions.and(order.requesterName.likeIgnoreCase("%" + criteria.getRequesterName() + "%"));
        }

        List<Order> query = queryFactory
            .selectFrom(order)
            .leftJoin(order.orderDetails, orderDetail).fetchJoin()
            .where(conditions)
            .orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Order.class))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        if (!query.isEmpty()) {
            List<UUID> orderIds = query.stream()
                .map(Order::getId)
                .toList();

            queryFactory
                .selectFrom(orderDetail)
                .where(orderDetail.order.id.in(orderIds))
                .fetch();
        }

        Long total = queryFactory
            .select(order.id.countDistinct())
            .from(order)
            .join(order.orderDetails, orderDetail)
            .where(conditions)
            .fetchOne();

        assert total != null;

        return new PageImpl<>(query, pageable, total);
    }

    @Override
    public Page<Order> findDynamicQueryForHubManager(OrderSearchCriteria criteria, Pageable pageable, UUID hubId) {
        BooleanBuilder conditions = new BooleanBuilder();

        conditions.and(order.requesterHubId.eq(hubId));

        if (criteria.getIsDeleted() != null) {
            conditions.and(order.isDeleted.eq(criteria.getIsDeleted()));
        } else {
            conditions.and(order.isDeleted.eq(false));
        }

        if (criteria.getProductName() != null && !criteria.getProductName().isEmpty()) {
            conditions.and(orderDetail.productName.likeIgnoreCase("%" + criteria.getProductName() + "%"));
        }

        if (criteria.getRecipientName() != null && !criteria.getRecipientName().isEmpty()) {
            conditions.and(orderDetail.recipientName.likeIgnoreCase("%" + criteria.getRecipientName() + "%"));
        }

        if (criteria.getRequesterName() != null && !criteria.getRequesterName().isEmpty()) {
            conditions.and(order.requesterName.likeIgnoreCase("%" + criteria.getRequesterName() + "%"));
        }

        List<Order> query = queryFactory
            .selectFrom(order)
            .leftJoin(order.orderDetails, orderDetail).fetchJoin()
            .where(conditions)
            .orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Order.class))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        if (!query.isEmpty()) {
            List<UUID> orderIds = query.stream()
                .map(Order::getId)
                .toList();

            queryFactory
                .selectFrom(orderDetail)
                .where(orderDetail.order.id.in(orderIds))
                .fetch();
        }

        Long total = queryFactory
            .select(order.id.countDistinct())
            .from(order)
            .join(order.orderDetails, orderDetail)
            .where(conditions)
            .fetchOne();

        assert total != null;

        return new PageImpl<>(query, pageable, total);
    }

    @Override
    public Page<Order> findDynamicQueryForOther(OrderSearchCriteria criteria, Pageable pageable, Long userId) {
        BooleanBuilder conditions = new BooleanBuilder();

        conditions.and(order.requesterUserId.eq(userId));

        if (criteria.getIsDeleted() != null) {
            conditions.and(order.isDeleted.eq(criteria.getIsDeleted()));
        }

        if (criteria.getProductName() != null && !criteria.getProductName().isEmpty()) {
            conditions.and(orderDetail.productName.likeIgnoreCase("%" + criteria.getProductName() + "%"));
        }

        if (criteria.getRecipientName() != null && !criteria.getRecipientName().isEmpty()) {
            conditions.and(orderDetail.recipientName.likeIgnoreCase("%" + criteria.getRecipientName() + "%"));
        }

        if (criteria.getRequesterName() != null && !criteria.getRequesterName().isEmpty()) {
            conditions.and(order.requesterName.likeIgnoreCase("%" + criteria.getRequesterName() + "%"));
        }

        List<Order> query = queryFactory
            .selectFrom(order)
            .leftJoin(order.orderDetails, orderDetail).fetchJoin()
            .where(conditions)
            .orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Order.class))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        if (!query.isEmpty()) {
            List<UUID> orderIds = query.stream()
                .map(Order::getId)
                .toList();

            queryFactory
                .selectFrom(orderDetail)
                .where(orderDetail.order.id.in(orderIds))
                .fetch();
        }

        Long total = queryFactory
            .select(order.id.countDistinct())
            .from(order)
            .join(order.orderDetails, orderDetail)
            .where(conditions)
            .fetchOne();

        assert total != null;

        return new PageImpl<>(query, pageable, total);
    }
}