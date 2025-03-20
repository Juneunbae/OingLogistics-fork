package com.oringmaryho.business.userservice.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;

public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {

}
