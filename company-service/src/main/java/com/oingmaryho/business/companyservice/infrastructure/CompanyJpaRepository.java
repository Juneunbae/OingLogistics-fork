package com.oingmaryho.business.companyservice.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oingmaryho.business.companyservice.domain.Company;

public interface CompanyJpaRepository extends JpaRepository<Company, UUID> {
	Optional<Company> findByIdAndIsDeletedFalse(UUID uuid);
}
