package com.service;

import com.model.Tournament;
import com.model.User;

public interface SchedulingService {
    /**
     * When a new player joins a tournament, create matches between them and
     * every other enrolled player, assigning a default referee (or rotating).
     */
    void createPairingsForNewPlayer(Tournament tournament, User newPlayer);
}
