// src/main/java/com/controller/MatchController.java
package com.controller;

import com.dto.MatchDTO;
import com.dto.ScoreDTO;
import com.model.Match;
import com.model.Role;
import com.repository.MatchRepository;
import com.repository.UserRepository;
import com.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "http://localhost:3000")
public class MatchController {

    private final MatchService matchService;
    private final MatchRepository matchRepo;
    private final UserRepository userRepository;

    public MatchController(
            MatchService matchService,
            MatchRepository matchRepo,
            UserRepository userRepository
    ) {
        this.matchService     = matchService;
        this.matchRepo        = matchRepo;
        this.userRepository   = userRepository;
    }

    // -----------------------
    // REFEREE endpoints
    // -----------------------

    @GetMapping("/referee/{refId}/upcoming")
    public ResponseEntity<List<MatchDTO>> getRefereeUpcoming(
            @PathVariable Long refId) {

        // 1) ensure referee exists and has REFEREE role
        return userRepository.findById(refId)
                .filter(u -> u.getRole() == Role.REFEREE)
                .map(u -> {
                    // 2) load and map
                    LocalDateTime now = LocalDateTime.now();
                    List<MatchDTO> dtos = matchRepo
                            .findByRefereeIdAndMatchDateTimeAfter(refId, now)
                            .stream()
                            .map(this::toDto)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(dtos);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/referee/{refId}/past")
    public ResponseEntity<List<MatchDTO>> getRefereePast(
            @PathVariable Long refId) {

        return userRepository.findById(refId)
                .filter(u -> u.getRole() == Role.REFEREE)
                .map(u -> {
                    LocalDateTime now = LocalDateTime.now();
                    List<MatchDTO> dtos = matchRepo
                            .findByRefereeIdAndMatchDateTimeBefore(refId, now)
                            .stream()
                            .map(this::toDto)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(dtos);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -----------------------
    // PLAYER endpoints
    // -----------------------

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<MatchDTO>> getPlayerAll(
            @PathVariable Long playerId) {

        // 1) ensure the player exists
        if (!userRepository.existsById(playerId)) {
            return ResponseEntity.notFound().build();
        }

        // 2) fetch & map all matches where they are either player1 or player2
        List<MatchDTO> dtos = matchRepo
                .findByPlayer1IdOrPlayer2Id(playerId, playerId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/player/{playerId}/upcoming")
    public ResponseEntity<List<MatchDTO>> getPlayerUpcoming(
            @PathVariable Long playerId) {

        // 1) ensure player exists
        if (!userRepository.existsById(playerId)) {
            return ResponseEntity.notFound().build();
        }

        // 2) filter by future date
        LocalDateTime now = LocalDateTime.now();
        List<MatchDTO> dtos = matchRepo
                .findByPlayer1IdOrPlayer2Id(playerId, playerId)
                .stream()
                .filter(m -> m.getMatchDateTime().isAfter(now))
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/player/{playerId}/past")
    public ResponseEntity<List<MatchDTO>> getPlayerPast(
            @PathVariable Long playerId) {

        if (!userRepository.existsById(playerId)) {
            return ResponseEntity.notFound().build();
        }

        LocalDateTime now = LocalDateTime.now();
        List<MatchDTO> dtos = matchRepo
                .findByPlayer1IdOrPlayer2Id(playerId, playerId)
                .stream()
                .filter(m -> m.getMatchDateTime().isBefore(now) || m.getMatchDateTime().isEqual(now))
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // -----------------------
    // Record a score (unchanged)
    // -----------------------

    @PostMapping("/{matchId}/score")
    public ResponseEntity<MatchDTO> recordScore(
            @PathVariable Long matchId,
            @RequestBody ScoreDTO dto) {

        Match updated = matchService.recordScore(matchId, dto.getScore());
        return ResponseEntity.ok(toDto(updated));
    }

    // -----------------------
    // DTO mapper
    // -----------------------

    private MatchDTO toDto(Match m) {
        return new MatchDTO(
                m.getId(),
                m.getMatchDateTime(),
                m.getPlayer1().getUsername(),
                m.getPlayer2().getUsername(),
                m.getScore()
        );
    }
}
