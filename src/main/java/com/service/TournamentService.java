package com.service;

import com.model.Tournament;
import com.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    /** Create a tournament without location */
    @Transactional
    public Tournament createTournament(String name,
                                       LocalDate startDate,
                                       LocalDate endDate) {
        Tournament t = new Tournament(name, startDate, endDate);
        return tournamentRepository.save(t);
    }

    /** Create a tournament with location */
    @Transactional
    public Tournament createTournament(String name,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       String location) {
        Tournament t = new Tournament(name, startDate, endDate, location);
        return tournamentRepository.save(t);
    }

    /** Find one by its id */
    @Transactional(readOnly = true)
    public Optional<Tournament> findById(Long id) {
        return tournamentRepository.findById(id);
    }

    /** List all tournaments */
    @Transactional(readOnly = true)
    public List<Tournament> findAll() {
        return tournamentRepository.findAll();
    }

    /** Update an existing tournament */
    @Transactional
    public Tournament updateTournament(Long id,
                                       String name,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       String location) {
        Tournament t = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found: " + id));
        t.setName(name);
        t.setStartDate(startDate);
        t.setEndDate(endDate);
        t.setLocation(location);
        return tournamentRepository.save(t);
    }

    /** Delete by id */
    @Transactional
    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }
}
