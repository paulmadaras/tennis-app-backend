// src/main/java/com/repository/UserRepository.java
package com.repository;

import com.dto.PlayerStatsDTO;
import com.model.User;
import com.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    /* needed by TournamentRegistrationService to e‑mail admins */
    List<User> findByRole(Role role);

    /**
     * Pull back every User with role = :role along with how many matches they've played.
     * We LEFT JOIN on matches where they are either player1 or player2.
     */
    @Query("""
      SELECT new com.dto.PlayerStatsDTO(
         u.id,
         u.username,
         u.fullName,
         u.email,
         COALESCE(COUNT(m),0)
      )
      FROM User u
      LEFT JOIN Match m 
        ON (m.player1 = u OR m.player2 = u)
      WHERE u.role = :role
      GROUP BY u.id
      """)
    List<PlayerStatsDTO> findAllWithMatchCountByRole(@Param("role") Role role);

}
