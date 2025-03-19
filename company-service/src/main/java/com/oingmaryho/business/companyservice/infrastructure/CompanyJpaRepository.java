package com.oingmaryho.business.companyservice.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.repository.CompanyRepository;

public interface CompanyJpaRepository extends JpaRepository<Company, UUID>, CompanyRepository {
}
