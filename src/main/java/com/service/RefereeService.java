// src/main/java/com/service/RefereeService.java
package com.service;

import com.dto.PlayerStatsDTO;
import com.model.User;

import java.util.List;

public interface RefereeService {

    public List<PlayerStatsDTO> findPlayersFiltered(int min, int max, String sort);
}
