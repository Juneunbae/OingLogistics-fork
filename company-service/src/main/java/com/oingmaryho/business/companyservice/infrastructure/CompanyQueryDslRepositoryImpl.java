package com.oingmaryho.business.companyservice.infrastructure;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;
import com.oingmaryho.business.companyservice.domain.QCompany;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class CompanyQueryDslRepositoryImpl implements CompanyQueryDslRepository {

	private final JPAQueryFactory queryFactory;

	public CompanyQueryDslRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public Page<Company> findDynamicQuery(CompanySearchCriteria searchCriteria, Pageable pageable) {
		QCompany company = QCompany.company;
		BooleanBuilder builder = buildSearchConditions(searchCriteria, company);

		List<Company> result = queryFactory
			.selectFrom(company)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.selectFrom(company)
			.where(builder)
			.fetchCount();

		return PageableExecutionUtils.getPage(result, pageable, () -> total);
	}

	private BooleanBuilder buildSearchConditions(CompanySearchCriteria searchCriteria, QCompany company) {
		BooleanBuilder builder = new BooleanBuilder();

		addIdCondition(builder, searchCriteria.getId(), company);
		addNameCondition(builder, searchCriteria.getName(), company);
		addTypeCondition(builder, searchCriteria.getType(), company);
		addManagerIdCondition(builder, searchCriteria.getManagerId(), company);
		addManageHubIdCondition(builder, searchCriteria.getManageHubId(), company);
		addAddressCondition(builder, searchCriteria.getAddress(), company);

		builder.and(company.isDeleted.isFalse()); // 삭제된 데이터 제외
		return builder;
	}

	private void addIdCondition(BooleanBuilder builder, UUID id, QCompany company) {
		if (id != null) {
			builder.and(company.id.eq(id));
		}
	}

	private void addNameCondition(BooleanBuilder builder, String name, QCompany company) {
		if (name != null) {
			builder.and(company.name.containsIgnoreCase(name));
		}
	}

	private void addTypeCondition(BooleanBuilder builder, String type, QCompany company) {
		if (type != null) {
			builder.and(company.type.eq(type));
		}
	}

	private void addManagerIdCondition(BooleanBuilder builder, Long managerId, QCompany company) {
		if (managerId != null) {
			builder.and(company.managerId.eq(managerId));
		}
	}

	private void addManageHubIdCondition(BooleanBuilder builder, UUID manageHubId, QCompany company) {
		if (manageHubId != null) {
			builder.and(company.manageHubId.eq(manageHubId));
		}
	}

	private void addAddressCondition(BooleanBuilder builder, String address, QCompany company) {
		if (address != null) {
			builder.and(company.address.containsIgnoreCase(address));
		}
	}
}
