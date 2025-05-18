// src/main/java/com/repository/TournamentUserRepository.java
package com.repository;

import com.model.TournamentUser;
import com.model.TournamentUserId;
import com.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentUserRepository
        extends JpaRepository<TournamentUser, TournamentUserId> {

    List<TournamentUser> findByStatus(RegistrationStatus status);

    /* used by TournamentRegistrationService */
    Optional<TournamentUser> findByUser_IdAndTournament_Id(
            Long userId, Long tournamentId
    );

    /* used by SchedulingServiceImpl */
    List<TournamentUser> findByTournament_Id(Long tournamentId);

    /* used by UserController when a player is deleted */
    void deleteByUser_Id(Long userId);
}
