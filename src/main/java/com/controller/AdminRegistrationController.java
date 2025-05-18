package com.controller;

import com.dto.PendingRegistrationsDTO;
import com.model.TournamentUser;
import com.service.TournamentRegistrationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/registrations")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRegistrationController {

    private final TournamentRegistrationService regService;

    public AdminRegistrationController(TournamentRegistrationService regService) {
        this.regService = regService;
    }

    /* ---------- list pending ---------- */
    @GetMapping
    public List<PendingRegistrationsDTO> listPending() {
        return regService.getPending()
                .stream()
                .map(r -> new PendingRegistrationsDTO(
                        r.getUser().getId(),
                        r.getUser().getUsername(),
                        r.getTournament().getId(),
                        r.getTournament().getName(),
                        r.getStatus(),
                        r.getRequestedAt()
                ))
                .collect(Collectors.toList());
    }

    /* ---------- approve / reject ---------- */
    @PutMapping("/{userId}/{tournamentId}/approve")
    public void approve(@PathVariable Long userId,
                        @PathVariable Long tournamentId) {
        regService.approve(userId, tournamentId);
    }

    @PutMapping("/{userId}/{tournamentId}/reject")
    public void reject(@PathVariable Long userId,
                       @PathVariable Long tournamentId) {
        regService.reject(userId, tournamentId);
    }
}
