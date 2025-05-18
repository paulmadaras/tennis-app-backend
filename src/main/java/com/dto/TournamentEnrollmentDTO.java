// src/main/java/com/dto/TournamentEnrollmentDTO.java
package com.dto;

import com.model.RegistrationStatus;

import java.time.LocalDate;

/**
 * Data returned to the front‑end for each tournament when a
 * player is logged–in.  “status” can be:
 *   • NONE      – user never asked to join
 *   • PENDING   – waiting for admin approval
 *   • APPROVED  – accepted (enrolled)
 */
public class TournamentEnrollmentDTO {

    private Long              id;
    private String            name;
    private LocalDate         startDate;
    private LocalDate         endDate;
    private RegistrationStatus status;

    /* ---------- constructor ---------- */
    public TournamentEnrollmentDTO(Long id,
                                   String name,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   RegistrationStatus status) {

        this.id        = id;
        this.name      = name;
        this.startDate = startDate;
        this.endDate   = endDate;
        this.status    = status;
    }

    /* ---------- getters / setters ---------- */
    public Long              getId()        { return id;        }
    public String            getName()      { return name;      }
    public LocalDate         getStartDate() { return startDate; }
    public LocalDate         getEndDate()   { return endDate;   }
    public RegistrationStatus getStatus()   { return status;    }

    public void setId(Long id)                       { this.id = id; }
    public void setName(String name)                 { this.name = name; }
    public void setStartDate(LocalDate startDate)    { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate)        { this.endDate = endDate; }
    public void setStatus(RegistrationStatus status) { this.status = status; }
}
