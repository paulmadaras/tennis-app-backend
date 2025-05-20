package com.repository;

import com.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id);

    // upcoming matches for a referee
    List<Match> findByRefereeIdAndMatchDateTimeAfter(Long refereeId, LocalDateTime now);

    // past matches for a referee that still need a score
    List<Match> findByRefereeIdAndMatchDateTimeBeforeAndScoreIsNull(Long refereeId, LocalDateTime now);

    List<Match> findByRefereeIdAndMatchDateTimeBefore(Long refereeId, LocalDateTime now);

    /** Delete all matches where a user participated as player1 or player2 */
    void deleteByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id);

    /** Delete all matches where a user was the referee */
    void deleteByRefereeId(Long refereeId);


    long countByPlayer1_IdOrPlayer2_Id(Long p1, Long p2);
}
