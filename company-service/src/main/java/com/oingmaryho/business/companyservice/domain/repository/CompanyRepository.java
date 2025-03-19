package com.oingmaryho.business.companyservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;

@Repository
public interface CompanyRepository {
	Optional<Company> findByIdAndIsDeletedFalse(UUID id);

	Page<Company> findDynamicQuery(CompanySearchCriteria searchCriteria, Pageable pageable);
}
