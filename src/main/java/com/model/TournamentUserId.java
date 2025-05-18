// src/main/java/com/model/TournamentUserId.java
package com.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TournamentUserId implements Serializable {
    private Long userId;
    private Long tournamentId;

    public TournamentUserId() {}

    public TournamentUserId(Long userId, Long tournamentId) {
        this.userId = userId;
        this.tournamentId = tournamentId;
    }

    // getters/setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TournamentUserId)) return false;
        TournamentUserId that = (TournamentUserId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(tournamentId, that.tournamentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tournamentId);
    }
}
