package com.config;

import com.model.Role;
import com.model.Tournament;
import com.model.User;
import com.service.TournamentRegistrationService;
import com.service.UserService;
import com.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Profile("!test")
public class DataInitializer implements ApplicationRunner {

    @Autowired private UserService userService;
    @Autowired private TournamentService tournamentService;
    @Autowired private TournamentRegistrationService regService;

    // hold references for cross-step use
    private List<User> seededPlayers;
    private List<Tournament> seededTournaments;

    @Override
    public void run(ApplicationArguments args) {
        // only initialize once
        if (userService.findAll().isEmpty()) {
            seedUsers();
        }
        if(tournamentService.findAll().isEmpty()) {
            seedTournaments();
        }
        if (regService.getApproved().isEmpty()) {
            seedRegistrations();
        }
    }

    /** seeds admin, referees, and players in their own transaction */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seedUsers() {
        // admin + referees
        userService.registerUser(buildUser("Alice Admin",   "Admin#123!",   Role.ADMIN));
        userService.registerUser(buildUser("Bob Referee",   "Referee#123!", Role.REFEREE));
        userService.registerUser(buildUser("Carol Referee", "Referee#124!", Role.REFEREE));

        // players
        List<String> playerNames = List.of(
                "Dave Player", "Eve Player", "Frank Player",
                "George Player", "Helen Player", "Ivan Player"
        );
        seededPlayers = playerNames.stream()
                .map(name -> userService.registerUser(
                        buildUser(name, "Player#123!", Role.TENNIS_PLAYER)))
                .collect(Collectors.toList());
    }

    /** seeds two tournaments in their own transaction */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seedTournaments() {
        // ensure we have the players
        if (seededPlayers == null || seededPlayers.isEmpty()) {
            seededPlayers = userService.findAll().stream()
                    .filter(u -> u.getRole() == Role.TENNIS_PLAYER)
                    .collect(Collectors.toList());
        }
        Tournament spring = tournamentService.createTournament(
                "Spring Open", LocalDate.of(2025,5,20), LocalDate.of(2025,5,25), "Bucharest");
        Tournament summer = tournamentService.createTournament(
                "Summer Cup",  LocalDate.of(2025,7,10), LocalDate.of(2025,7,15), "Cluj");
        seededTournaments = List.of(spring, summer);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seedRegistrations() {
        // ensure we have the tournaments
        if (seededTournaments == null || seededTournaments.size() < 2) {
            seededTournaments = tournamentService.findAll();
        }
        // ensure we have the players
        if (seededPlayers == null || seededPlayers.isEmpty()) {
            seededPlayers = userService.findAll().stream()
                    .filter(u -> u.getRole() == Role.TENNIS_PLAYER)
                    .collect(Collectors.toList());
        }
        Tournament spring = seededTournaments.get(0);
        Tournament summer = seededTournaments.get(1);

        long springId = spring.getId();
        long summerId = summer.getId();

        // enroll first half of players in Spring, second half in Summer
        int midpoint = seededPlayers.size() / 2;
        List<User> springPlayers = seededPlayers.subList(0, midpoint);
        List<User> summerPlayers = seededPlayers.subList(midpoint, seededPlayers.size());

        for (User p : springPlayers) {
            try {
                regService.register(p.getId(), springId);
                regService.approve(p.getId(), springId);
            } catch (Exception ex) {
                System.err.println("Failed to register " + p.getUsername()
                        + " for " + spring.getName() + ": " + ex.getMessage());
            }
        }
        for (User p : summerPlayers) {
            try {
                regService.register(p.getId(), summerId);
                regService.approve(p.getId(), summerId);
            } catch (Exception ex) {
                System.err.println("Failed to register " + p.getUsername()
                        + " for " + summer.getName() + ": " + ex.getMessage());
            }
        }
    }

    /** Helper: builds a User with username=first_last */
    private User buildUser(String fullName, String rawPassword, Role role) {
        String[] parts = fullName.split(" ");
        String username = parts[0].toLowerCase() + "_" + parts[1].toLowerCase();

        User u = new User();
        u.setFullName(fullName);
        u.setUsername(username);
        u.setEmail(username + "@example.com");
        u.setPassword(rawPassword);
        u.setRole(role);
        return u;
    }
}
