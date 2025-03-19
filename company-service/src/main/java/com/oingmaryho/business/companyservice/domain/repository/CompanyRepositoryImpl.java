package com.oingmaryho.business.companyservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.domain.CompanySearchCriteria;
import com.oingmaryho.business.companyservice.infrastructure.CompanyJpaRepository;
import com.oingmaryho.business.companyservice.infrastructure.CompanyQueryDslRepository;

@Repository
public class CompanyRepositoryImpl implements CompanyRepository {

	private final CompanyJpaRepository companyJpaRepository;
	private final CompanyQueryDslRepository companyQueryDslRepository;

	public CompanyRepositoryImpl(CompanyJpaRepository companyJpaRepository, CompanyQueryDslRepository companyQueryDslRepository) {
		this.companyJpaRepository = companyJpaRepository;
		this.companyQueryDslRepository = companyQueryDslRepository;
	}
	@Override
	public Optional<Company> findByIdAndIsDeletedFalse(UUID id){
		return companyJpaRepository.findByIdAndIsDeletedFalse(id);
	}

	@Override
	public Page<Company> findDynamicQuery(CompanySearchCriteria searchCriteria, Pageable pageable){
		return companyQueryDslRepository.findDynamicQuery(searchCriteria,pageable);
	}

	@Override
	public Company save(Company company) {
		return companyJpaRepository.save(company);
	}
}


