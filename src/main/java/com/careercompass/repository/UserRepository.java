package com.careercompass.repository;

import com.careercompass.entity.User;
import com.careercompass.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByState(String state);

    List<User> findByEnabled(Boolean enabled);

    boolean existsByEmail(String email);
}
