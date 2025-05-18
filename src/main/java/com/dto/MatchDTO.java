// src/main/java/com/dto/MatchDTO.java
package com.dto;

import java.time.LocalDateTime;

public class MatchDTO {
    private Long id;
    private LocalDateTime matchDateTime;
    private String player1;
    private String player2;
    private String score;  // null for upcoming

    public MatchDTO(Long id, LocalDateTime matchDateTime,
                    String player1, String player2, String score) {
        this.id = id;
        this.matchDateTime = matchDateTime;
        this.player1 = player1;
        this.player2 = player2;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getMatchDateTime() {
        return matchDateTime;
    }

    public void setMatchDateTime(LocalDateTime matchDateTime) {
        this.matchDateTime = matchDateTime;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
