package com.oringmaryho.business.userservice.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.domain.UserSearchCriteria;

public interface CustomUserRepository {

	Page<User> findDynamicQuery(UserSearchCriteria criteria, Pageable pageable);

	Optional<User> findActiveUserById(Long id);

	Optional<List<User>> findUsersByRole(UserRoleType role, Boolean isDeleted);
}
