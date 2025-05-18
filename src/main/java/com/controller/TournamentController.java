package com.controller;

import com.dto.TournamentEnrollmentDTO;
import com.model.*;
import com.repository.*;
import com.service.TournamentRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "http://localhost:3000")
public class TournamentController {

    private final TournamentRepository     tournamentRepo;
    private final TournamentUserRepository regRepo;
    private final UserRepository           userRepo;
    private final TournamentRegistrationService regService;

    public TournamentController(TournamentRepository     tournamentRepo,
                                TournamentUserRepository regRepo,
                                UserRepository           userRepo,
                                TournamentRegistrationService regService) {
        this.tournamentRepo = tournamentRepo;
        this.regRepo        = regRepo;
        this.userRepo       = userRepo;
        this.regService     = regService;
    }

    /* ---------- list for player ---------- */
    @GetMapping("/user/{uid}")
    public ResponseEntity<List<TournamentEnrollmentDTO>>
    list(@PathVariable Long uid) {

        User user = userRepo.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<TournamentEnrollmentDTO> dto = tournamentRepo.findAll().stream()
                .map(t -> {
                    RegistrationStatus st = regRepo
                            .findByUser_IdAndTournament_Id(uid, t.getId())
                            .map(TournamentUser::getStatus)
                            .orElse(RegistrationStatus.NONE);
                    return new TournamentEnrollmentDTO(
                            t.getId(), t.getName(), t.getStartDate(), t.getEndDate(), st);
                }).toList();

        return ResponseEntity.ok(dto);
    }

    /* ---------- player clicks “Register” ---------- */
    @PostMapping("/{tid}/enroll/{uid}")
    public ResponseEntity<?> enroll(@PathVariable Long tid, @PathVariable Long uid) {
        try {
            regService.register(uid, tid);
            return ResponseEntity.ok("Request saved.");
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
