package com.oingmaryho.business.delivery_service.infrastructure;

import com.oingmaryho.business.delivery_service.config.querydsl.QueryDslUtils;
import com.oingmaryho.business.delivery_service.domain.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryCustomRepositoryImpl implements DeliveryCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Delivery> searchDelivery(UUID hubId,                      // 허브 id
                                         UUID companyId,                  // 업체 id
                                         UUID managerId,                  // 배송 담당자 id
                                         DeliveryManagerType managerType, // 배송 담당자 타입
                                         Pageable pageable) {
        QDelivery qDelivery = QDelivery.delivery;
        QDeliveryRoute qDeliveryRoute = QDeliveryRoute.deliveryRoute;
        QDeliveryManager qDeliveryManager = QDeliveryManager.deliveryManager;

        BooleanBuilder builder = new BooleanBuilder();

        if (hubId != null) {        // 허브 관리자가 허브 id로 조회
            builder.and(qDelivery.departureHubId.eq(hubId));
        }

        if (companyId != null) {    // 업체 관리자가 업체 id로 조회
            builder.and(qDelivery.managerId.in(
                    queryFactory.select(qDeliveryManager.id)
                            .from(qDeliveryManager)
                            .where(qDeliveryManager.companyId.eq(companyId))
            ));
        }

        if (managerId != null && managerType != null) {
            if (managerType.equals(DeliveryManagerType.HUB_DELIVERY_MANAGER)) {         // 허브 배송 담당자인 경우
                builder.and(qDeliveryRoute.managerId.eq(managerId));
            }
            if (managerType.equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {     // 업체 배송 담당자인 경우
                builder.and(qDelivery.managerId.eq(managerId));
            }
        }


        // 조회 쿼리
        List<Delivery> deliveries = queryFactory.selectDistinct(qDelivery)
                .from(qDelivery)
                .join(qDeliveryRoute).on(qDeliveryRoute.delivery.eq(qDelivery))
                .where(builder)
                .orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Delivery.class))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        // Count 쿼리
        Long total = queryFactory.select(qDelivery.id.count())
                .from(qDelivery)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                deliveries,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                total != null ? total : 0L);

    }
}
