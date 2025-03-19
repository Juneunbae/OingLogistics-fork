package com.oringmaryho.business.userservice.infrastructure;

import java.util.Optional;

import com.oringmaryho.business.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);


}
