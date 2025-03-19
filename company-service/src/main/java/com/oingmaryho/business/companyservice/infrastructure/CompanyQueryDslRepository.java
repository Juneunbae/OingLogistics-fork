package com.oingmaryho.business.companyservice.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;

public interface CompanyQueryDslRepository {
	Page<Company> findDynamicQuery(CompanySearchCriteria searchCriteria, Pageable pageable);
}
