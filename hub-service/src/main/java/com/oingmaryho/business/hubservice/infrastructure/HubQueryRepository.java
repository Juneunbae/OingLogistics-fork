package com.oingmaryho.business.hubservice.infrastructure;

import static com.oingmaryho.business.hubservice.domain.QHub.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubSearchCriteria;
import com.oingmaryho.business.hubservice.domain.repository.CustomHubRepository;
import com.oingmaryho.business.hubservice.utils.QueryDslUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubQueryRepository implements CustomHubRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Hub> findDynamicQuery(HubSearchCriteria criteria, Pageable pageable) {
		JPAQuery<Hub> query = queryFactory
			.selectFrom(hub)
			.where(
				eqId(criteria.getId()),
				eqName(criteria.getName()),
				eqAddress(criteria.getAddress()),
				eqLatitude(criteria.getLatitude()),
				eqLongitude(criteria.getLongitude()),
				eqManagerId(criteria.getManagerId()),
				eqIsDeleted(criteria.getIsDeleted())
			)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Hub.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		List<Hub> results = query.fetch();
		return new PageImpl<>(results, pageable, results.size());
	}

	@Override
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

	private BooleanExpression eqId(UUID id) {
		if (id == null) {
			return null;
		}
		return hub.id.eq(id);
	}

	private BooleanExpression eqName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return hub.name.eq(name);
	}

	private BooleanExpression eqAddress(String address) {
		if (StringUtils.isEmpty(address)) {
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
