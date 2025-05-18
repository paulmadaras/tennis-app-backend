// src/main/java/com/repository/UserRepository.java
package com.repository;

import com.model.User;
import com.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    /* needed by TournamentRegistrationService to e‑mail admins */
    List<User> findByRole(Role role);
}
