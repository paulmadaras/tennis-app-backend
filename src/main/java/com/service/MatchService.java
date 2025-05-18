package com.service;
import com.model.Match;
import com.validation.ScoreValidator;
import com.repository.MatchRepository;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;


@Service
public class MatchService {
    private final MatchRepository matchRepo;
    private final ScoreValidator scoreValidator;

    public MatchService(MatchRepository matchRepo, ScoreValidator scoreValidator) {
        this.matchRepo = matchRepo;
        this.scoreValidator = scoreValidator;
    }

    /** Called by a referee or admin to record the final score **/
    public Match recordScore(Long matchId, String score) {
        // validate format first
        scoreValidator.validate(score);

        Match m = matchRepo.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
        m.setScore(score);
        return matchRepo.save(m);
    }
}
