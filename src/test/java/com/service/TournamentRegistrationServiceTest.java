// src/test/java/com/service/TournamentRegistrationServiceIntegrationTest.java
package com.service;

import com.model.*;
import com.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TournamentRegistrationServiceIntegrationTest {

    @TestConfiguration
    static class StubMailConfig {
        @Bean
        public JavaMailSender mailSender() {
            return new JavaMailSenderImpl();  // default, no-ops are fine
        }
    }

    @Autowired private TournamentRegistrationService regService;
    @Autowired private TournamentRepository tournamentRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private TournamentUserRepository tuRepo;

    private User player;
    private Tournament tour;

    @BeforeEach
    void setUp() {
        player = userRepo.save(new User("p1","p1@example.com","pw",Role.TENNIS_PLAYER,"Player One"));
        tour   = tournamentRepo.save(new Tournament("Test Cup",
                LocalDate.of(2025,6,1), LocalDate.of(2025,6,5)));
    }

    @Test
    void registerCreatesPending() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: a player signs up for a tournament.
        // Expectation: a new TournamentUser record is created with status=PENDING.
        // ────────────────────────────────────────────────────────────────────────────

        // 1) Invoke the registration method
        regService.register(player.getId(), tour.getId());

        // 2) Fetch all registrations currently marked PENDING
        List<TournamentUser> regs = tuRepo.findByStatus(RegistrationStatus.PENDING);

        //    → Exactly one pending registration should exist
        assertEquals(1, regs.size());

        //    → That registration should belong to our test player
        assertEquals(player.getId(), regs.get(0).getUser().getId());
    }

    @Test
    void approveUpdatesStatus() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: a pending registration is approved.
        // Expectation: the TournamentUser status is changed to APPROVED.
        // ────────────────────────────────────────────────────────────────────────────

        // 1) First, create the pending registration
        regService.register(player.getId(), tour.getId());

        // 2) Approve it
        regService.approve(player.getId(), tour.getId());

        // 3) Look up that specific registration
        var reg = tuRepo
                .findByUser_IdAndTournament_Id(player.getId(), tour.getId())
                .orElseThrow();

        //    → The status should now be APPROVED
        assertEquals(RegistrationStatus.APPROVED, reg.getStatus());
    }

    @Test
    void rejectUpdatesStatus() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: a pending registration is rejected.
        // Expectation: the TournamentUser status is changed to REJECTED.
        // ────────────────────────────────────────────────────────────────────────────

        // 1) Create the pending registration
        regService.register(player.getId(), tour.getId());

        // 2) Reject it
        regService.reject(player.getId(), tour.getId());

        // 3) Fetch the registration again
        var reg = tuRepo
                .findByUser_IdAndTournament_Id(player.getId(), tour.getId())
                .orElseThrow();

        //    → The status should now be REJECTED
        assertEquals(RegistrationStatus.REJECTED, reg.getStatus());
    }

    @Test
    void duplicateRegistration_throws() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: the same player tries to register twice for the same tournament.
        // Expectation: the second call throws an IllegalStateException.
        // ────────────────────────────────────────────────────────────────────────────

        // 1) First registration succeeds
        regService.register(player.getId(), tour.getId());

        // 2) Attempting to register again must fail
        assertThrows(
                IllegalStateException.class,
                () -> regService.register(player.getId(), tour.getId())
        );
    }

}
