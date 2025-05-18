package com.dto;

import com.model.RegistrationStatus;
import java.time.Instant;

public class PendingRegistrationsDTO {

    private Long userId;
    private String username;
    private Long tournamentId;
    private String tournamentName;
    private RegistrationStatus status;
    private Instant requestedAt;

    public PendingRegistrationsDTO(Long userId,
                                       String username,
                                       Long tournamentId,
                                       String tournamentName,
                                       RegistrationStatus status,
                                       Instant requestedAt) {
        this.userId          = userId;
        this.username        = username;
        this.tournamentId    = tournamentId;
        this.tournamentName  = tournamentName;
        this.status          = status;
        this.requestedAt     = requestedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }


}
