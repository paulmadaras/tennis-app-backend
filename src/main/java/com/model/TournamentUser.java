package com.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tournament_users")
public class TournamentUser {

    /* ---------- composite PK ---------- */
    @EmbeddedId
    private TournamentUserId id;

    /* ---------- owning sides ---------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tournamentId")
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    /* ---------- registration meta ---------- */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    private Instant requestedAt = Instant.now();

    /** JPA ctor */
    protected TournamentUser() {}

    /** canonical ctor – used by RegistrationService */
    public TournamentUser(TournamentUserId id,
                          User user,
                          Tournament tournament,
                          RegistrationStatus status,
                          Instant requestedAt) {
        this.id          = id;
        this.user        = user;
        this.tournament  = tournament;
        this.status      = status;
        this.requestedAt = requestedAt;
    }

    /** convenience ctor (legacy) */
    public TournamentUser(User user, Tournament tournament) {
        this(new TournamentUserId(user.getId(), tournament.getId()),
                user, tournament, RegistrationStatus.PENDING, Instant.now());
    }

    /* ---------- getters / setters ---------- */
    public TournamentUserId getId()             { return id; }
    public User            getUser()           { return user; }
    public Tournament      getTournament()     { return tournament; }
    public RegistrationStatus getStatus()      { return status; }
    public void            setStatus(RegistrationStatus s){ this.status = s; }
    public Instant         getRequestedAt()    { return requestedAt; }
    public void            setRequestedAt(Instant t)      { this.requestedAt = t; }
}
