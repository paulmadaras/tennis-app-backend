// src/main/java/com/dto/PlayerStatsDTO.java
package com.dto;

public class PlayerStatsDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private long matchCount;

    public PlayerStatsDTO(Long id, String username, String fullName, String email, long matchCount) {
        this.id         = id;
        this.username   = username;
        this.fullName   = fullName;

        this.email      = email;
        this.matchCount = matchCount;
    }
    // ← getters only
    public Long   getId()         { return id; }
    public String getUsername()   { return username; }
    public String getFullName()   { return fullName; }
    public String getEmail()      { return email; }
    public long   getMatchCount() { return matchCount; }
}
