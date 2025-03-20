package com.oringmaryho.business.userservice.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oringmaryho.business.userservice.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);

}
