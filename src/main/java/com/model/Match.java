// src/main/java/com/model/Match.java
package com.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_date_time", nullable = false)
    private LocalDateTime matchDateTime;

    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private User player1;

    @ManyToOne
    @JoinColumn(name = "player2_id", nullable = false)
    private User player2;

    @ManyToOne
    @JoinColumn(name = "referee_id", nullable = false)
    private User referee;

    /**
     * Score is nullable until the referee/admin records it.
     */
    @Column(nullable = true)
    private String score;

    public Match() {}

    public Match(LocalDateTime matchDateTime, User player1, User player2, User referee, String score) {
        this.matchDateTime = matchDateTime;
        this.player1       = player1;
        this.player2       = player2;
        this.referee       = referee;
        this.score         = score;
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public LocalDateTime getMatchDateTime() {
        return matchDateTime;
    }

    public void setMatchDateTime(LocalDateTime matchDateTime) {
        this.matchDateTime = matchDateTime;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public User getReferee() {
        return referee;
    }

    public void setReferee(User referee) {
        this.referee = referee;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
