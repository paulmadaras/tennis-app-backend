// src/test/java/com/service/SchedulingServiceImplIntegrationTest.java
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SchedulingServiceImplIntegrationTest {

    @TestConfiguration
    static class StubMailConfig {
        @Bean
        public JavaMailSender mailSender() {
            return new JavaMailSenderImpl();  // default, no-ops are fine
        }
    }

    @Autowired
    private MatchRepository matchRepo;
    @Autowired
    private TournamentUserRepository regRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private TournamentRepository tourRepo;
    @Autowired
    private SchedulingService schedulingService;
    @Autowired
    private TournamentRegistrationService regService;

    private Tournament tour;
    private User u1, u2, ref;

    @BeforeEach
    void setUp() {
        // seed a fresh tournament and two players
        tour = tourRepo.save(new Tournament("Test Cup", LocalDate.now(), LocalDate.now().plusDays(1), "Somewhere"));
        u1   = userRepo.save(new User("alice", "alice@example.com", "Pass123!", Role.TENNIS_PLAYER, "Alice Wonder"));
        u2   = userRepo.save(new User("bob",   "bob@example.com",   "Pass123!",   Role.TENNIS_PLAYER,"Bob Builder"));
        ref = userRepo.save(new User("jack_ref","ref@example.com","RefPass123!",Role.REFEREE,"Jack Referee"));

    }

    @Test
    void singlePairing_createdWhenSecondJoins() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: you register/approve two players in the same tournament back‐to‐back.
        // Expectation: when the *second* player is approved, exactly one Match is created
        //               (the pair between player1 and player2).
        // ────────────────────────────────────────────────────────────────────────────

        // 1) First player registers & is approved
        regService.register(u1.getId(), tour.getId());
        regService.approve (u1.getId(), tour.getId());

        //    → Since there's only one player so far, no match should be generated
        assertEquals(0, matchRepo.findAll().size());

        // 2) Second player registers & is approved
        regService.register(u2.getId(), tour.getId());
        regService.approve (u2.getId(), tour.getId());

        //    → Now that two players are in the field, exactly one pairing should exist
        assertEquals(1, matchRepo.findAll().size());
    }

    @Test
    void noPairing_ifOnlyOnePlayer() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: you invoke the scheduling service directly with a single player.
        // Expectation: it should never create a match if there is only one participant.
        // ────────────────────────────────────────────────────────────────────────────

        // Make sure the match table is empty before we start
        matchRepo.deleteAll();

        // Call the pairing logic “as if” u1 has just joined, but there are no others
        schedulingService.createPairingsForNewPlayer(tour, u1);

        // Since u1 is alone, no matches should be created
        assertTrue(matchRepo.findAll().isEmpty());
    }

}
