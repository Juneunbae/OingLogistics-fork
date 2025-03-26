package com.oingmaryho.business.companyservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanyType;

@Repository
public interface CompanyRepository {
	Optional<Company> findByIdAndIsDeletedFalse(UUID id);
	boolean existsByTypeAndAddressAndIsDeletedFalse(String productCode, CompanyType companyType);
	Company save(Company company);
}
