package com.oingmaryho.business.companyservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.oingmaryho.business.companyservice.domain.Company;
import com.oingmaryho.business.companyservice.infrastructure.CompanyJpaRepository;

@Repository
public class CompanyRepositoryImpl implements CompanyRepository {

	private final CompanyJpaRepository companyJpaRepository;

	public CompanyRepositoryImpl(CompanyJpaRepository companyJpaRepository) {
		this.companyJpaRepository = companyJpaRepository;
	}
	@Override
	public Optional<Company> findByIdAndIsDeletedFalse(UUID id){
		return companyJpaRepository.findByIdAndIsDeletedFalse(id);
	}
}


