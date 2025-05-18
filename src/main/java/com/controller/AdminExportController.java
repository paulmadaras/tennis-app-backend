// src/main/java/com/controller/AdminExportController.java
package com.controller;

import com.model.Match;
import com.repository.MatchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminExportController {

    private final MatchRepository matchRepository;

    public AdminExportController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /**
     * Export all matches in CSV or TXT format.
     * GET /api/admin/matches/export?format=csv
     */
    @GetMapping("/matches/export")
    public void exportMatches(
            @RequestParam(name = "format", defaultValue = "csv") String format,
            HttpServletResponse response) throws IOException {

        List<Match> matches = matchRepository.findAll();

        if ("csv".equalsIgnoreCase(format)) {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"matches.csv\"");
            try (PrintWriter writer = response.getWriter()) {
                // header
                writer.println("Id, MatchDateTime, Player1, Player2, Referee, Score");
                // rows
                for (Match m : matches) {
                    String safeScore = m.getScore() != null
                            ? m.getScore().replaceAll(",", ";")
                            : "";
                    writer.printf("%d, %s, %s, %s, %s, %s%n",
                            m.getId(),
                            m.getMatchDateTime(),
                            m.getPlayer1().getUsername(),
                            m.getPlayer2().getUsername(),
                            m.getReferee().getUsername(),
                            safeScore
                    );
                }
            }
        } else if ("txt".equalsIgnoreCase(format)) {
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=\"matches.txt\"");
            try (PrintWriter writer = response.getWriter()) {
                for (Match m : matches) {
                    String line = String.format(
                            "Match %d: %s vs %s at %s, Referee: %s, Score: %s",
                            m.getId(),
                            m.getPlayer1().getUsername(),
                            m.getPlayer2().getUsername(),
                            m.getMatchDateTime(),
                            m.getReferee().getUsername(),
                            m.getScore() != null ? m.getScore() : "TBD"
                    );
                    writer.println(line);
                }
            }
        } else {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Unsupported format: " + format);
        }
    }
}
