package com.oingmaryho.business.hubservice.infrastructure.repository.hub;

import static com.oingmaryho.business.hubservice.domain.QHub.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.criteria.HubSearchCriteria;
import com.oingmaryho.business.hubservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<Hub> findDynamicQuery(HubSearchCriteria criteria, Pageable pageable) {
		BooleanBuilder conditions = new BooleanBuilder();
		conditions
			.and(eqId(criteria.getId()))
			.and(containsName(criteria.getName()))
			.and(containsAddress(criteria.getAddress()))
			.and(eqLatitude(criteria.getLatitude()))
			.and(eqLongitude(criteria.getLongitude()))
			.and(eqManagerId(criteria.getManagerId()))
			.and(eqIsDeleted(criteria.getIsDeleted()));

		JPAQuery<Hub> query = queryFactory
			.selectFrom(hub)
			.where(conditions)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Hub.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		Long total = queryFactory
			.select(hub.id.count())
			.from(hub)
			.where(conditions)
			.fetchOne();
		assert total != null;

		return new PageImpl<>(query.fetch(), pageable, total);
	}

	public Optional<Hub> findActiveHubById(UUID id) {
		return Optional.ofNullable(queryFactory
			.selectFrom(hub)
			.where(
				eqId(id),
				eqIsDeleted(Boolean.FALSE)
			)
			.fetchOne()
		);
	}

	public List<Hub> findAllActiveHubs() {
		return queryFactory
			.selectFrom(hub)
			.where(eqIsDeleted(Boolean.FALSE))
			.fetch();
	}

	private BooleanExpression eqId(UUID id) {
		if (id == null) {
			return null;
		}
		return hub.id.eq(id);
	}

	private BooleanExpression containsName(String name) {
		if (!StringUtils.hasText(name)) {
			return null;
		}
		return hub.name.contains(name);
	}

	private BooleanExpression containsAddress(String address) {
		if (!StringUtils.hasText(address)) {
			return null;
		}
		return hub.address.address.contains(address);
	}

	// TODO : 위도, 경도 검색 다시 생각해보기
	private BooleanExpression eqLatitude(Double latitude) {
		if (latitude == null) {
			return null;
		}
		return hub.address.latitude.eq(latitude);
	}

	private BooleanExpression eqLongitude(Double longitude) {
		if (longitude == null) {
			return null;
		}
		return hub.address.longitude.eq(longitude);
	}

	private BooleanExpression eqManagerId(Long managerId) {
		if (managerId == null) {
			return null;
		}
		return hub.managerId.eq(managerId);
	}

	private BooleanExpression eqIsDeleted(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		return hub.isDeleted.eq(isDeleted);
	}
}
