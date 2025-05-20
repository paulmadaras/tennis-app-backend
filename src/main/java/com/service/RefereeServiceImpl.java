// src/main/java/com/service/RefereeServiceImpl.java
package com.service;

import com.dto.PlayerStatsDTO;
import com.model.Role;
import com.model.User;
import com.repository.MatchRepository;
import com.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RefereeServiceImpl implements RefereeService {

    private final UserRepository   userRepo;

    public RefereeServiceImpl(UserRepository userRepo) {
        this.userRepo  = userRepo;
    }

    @Override
    public List<PlayerStatsDTO> findPlayersFiltered(int minMatches, int maxMatches, String sortBy) {
        // 1) fetch only players + their matchCount
        List<PlayerStatsDTO> all = userRepo.findAllWithMatchCountByRole(Role.TENNIS_PLAYER);

        // 2) apply min/max filter
        var filtered = all.stream()
                .filter(p -> p.getMatchCount() >= minMatches
                        && p.getMatchCount() <= maxMatches);

        // 3) pick comparator
        Comparator<PlayerStatsDTO> cmp = switch (sortBy) {
            case "matchesDesc" -> Comparator
                    .comparingLong(PlayerStatsDTO::getMatchCount)
                    .reversed();
            case "matchesAsc"  -> Comparator
                    .comparingLong(PlayerStatsDTO::getMatchCount);
            case "alpha"      -> Comparator
                    .comparing(PlayerStatsDTO::getUsername, String.CASE_INSENSITIVE_ORDER);
            default           -> Comparator
                    .comparing(PlayerStatsDTO::getUsername, String.CASE_INSENSITIVE_ORDER);
        };


        // 4) sort & collect
        return filtered
                .sorted(cmp)
                .collect(Collectors.toList());
    }

}
