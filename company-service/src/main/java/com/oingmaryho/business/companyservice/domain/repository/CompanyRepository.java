package com.oingmaryho.business.companyservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanyType;

@Repository
public interface CompanyRepository {
	Optional<Company> findByIdAndIsDeletedFalse(UUID id);
	Optional<Company> findByManagerIdAndIsDeletedFalse(Long userId);
	boolean existsByTypeAndAddressAndIsDeletedFalse(CompanyType companyType, String address);
	Company save(Company company);
}
