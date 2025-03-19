package com.oingmaryho.business.companyservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;

public interface CustomCompanyRepository {
	Page<Company> findDynamicQuery(CompanySearchCriteria searchCriteria, Pageable pageable);
}
