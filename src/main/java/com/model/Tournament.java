package com.model;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;            // if you need it

    /* many-to-many with users who enrolled */
    @ManyToMany
    @JoinTable(
            name = "tournament_users",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> enrolledUsers = new HashSet<>();

    /** no-arg ctor required by JPA */
    public Tournament() {}

    /** 3-arg convenience constructor */
    public Tournament(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** 4-arg constructor (if you need location) */
    public Tournament(String name, LocalDate startDate, LocalDate endDate, String location) {
        this(name, startDate, endDate);          // delegate to 3-arg
        this.location = location;
    }

    // --- getters & setters ---
    public Set<User> getEnrolledUsers() { return enrolledUsers; }
    public void setEnrolledUsers(Set<User> enrolledUsers) { this.enrolledUsers = enrolledUsers; }

    /* plus getters/setters for id, name, dates, location … */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

