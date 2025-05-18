package com.service;

import com.model.*;
import com.repository.MatchRepository;
import com.repository.TournamentUserRepository;
import com.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class SchedulingServiceImpl implements SchedulingService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final TournamentUserRepository tournamentUserRepository;

    public SchedulingServiceImpl(MatchRepository matchRepository,
                                 UserRepository userRepository,
                                 TournamentUserRepository tournamentUserRepository) {
        this.matchRepository            = matchRepository;
        this.userRepository             = userRepository;
        this.tournamentUserRepository   = tournamentUserRepository;
    }

    /**
     * When a new player joins (i.e. gets APPROVED), create matches...
     *  - If ≤6 existing players → match vs. all of them.
     *  - If >6 existing players → pick 6 at random.
     */
    @Override
    @Transactional
    public void createPairingsForNewPlayer(Tournament tournament, User newPlayer) {
        // 1) Fetch only APPROVED registrations for this tournament
        List<User> existingPlayers = tournamentUserRepository
                .findByTournament_Id(tournament.getId()).stream()
                .filter(tu -> tu.getStatus() == RegistrationStatus.APPROVED)
                .map(TournamentUser::getUser)
                .filter(u -> !u.getId().equals(newPlayer.getId()))
                .collect(Collectors.toList());

        if (existingPlayers.isEmpty()) {
            // no one to pair with
            return;
        }

        // 2) Decide which opponents: all if ≤6, otherwise 6 at random
        List<User> opponents;
        if (existingPlayers.size() <= 6) {
            opponents = existingPlayers;
        } else {
            Collections.shuffle(existingPlayers);
            opponents = existingPlayers.subList(0, 6);
        }

        // 3) Pick any REFEREE
        List<User> referees = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.REFEREE)
                .toList();

        // Pick one at random, or null if none
        User referee = null;
        if (!referees.isEmpty()) {
            int idx = ThreadLocalRandom.current().nextInt(referees.size());
            referee = referees.get(idx);
        }

        // 4) Create a match vs. each selected opponent
        for (User opponent : opponents) {
            LocalDateTime when = LocalDateTime.now()
                    .plusMinutes((long)(Math.random() * 1000));
            Match m = new Match(when, newPlayer, opponent, referee, null);
            matchRepository.save(m);
        }
    }
}
