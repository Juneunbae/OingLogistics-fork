package com.oingmaryho.business.companyservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.oingmaryho.business.companyservice.domain.Company;

@Repository
public interface CompanyRepository {
	Optional<Company> findByIdAndIsDeletedFalse(UUID id);
	boolean existsByAddressAndIsDeletedFalse(String productCode);
	Company save(Company company);
}
