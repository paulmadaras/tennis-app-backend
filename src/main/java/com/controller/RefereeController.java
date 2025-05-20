// src/main/java/com/controller/RefereeController.java
package com.controller;

import com.dto.PlayerStatsDTO;
import com.service.RefereeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/referee")
public class RefereeController {

    private final RefereeService refereeService;

    public RefereeController(RefereeService refereeService) {
        this.refereeService = refereeService;
    }

    @GetMapping("/players")
    public List<PlayerStatsDTO> listPlayers(
            @RequestParam(defaultValue = "0")       int minMatches,
            @RequestParam(defaultValue = "999999")  int maxMatches,
            @RequestParam(defaultValue = "alpha")   String sortBy
    ) {
        return refereeService.findPlayersFiltered(
                minMatches,
                maxMatches,
                sortBy
        );
    }
}

