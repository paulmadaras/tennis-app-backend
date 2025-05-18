package com.service;

import com.model.*;
import com.repository.*;
import com.service.SchedulingServiceImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TournamentRegistrationService {

    private final TournamentUserRepository regRepo;
    private final TournamentRepository     tournamentRepo;
    private final UserRepository           userRepo;
    private final JavaMailSender           mailSender;
    private final SchedulingServiceImpl schedulingService;

    public TournamentRegistrationService(
            TournamentUserRepository regRepo,
            TournamentRepository     tournamentRepo,
            UserRepository           userRepo,
            JavaMailSender           mailSender, SchedulingServiceImpl schedulingService) {

        this.regRepo        = regRepo;
        this.tournamentRepo = tournamentRepo;
        this.userRepo       = userRepo;
        this.mailSender     = mailSender;
        this.schedulingService = schedulingService;
    }

    /* ------------------------------------------------------------------ */
    /*  PLAYER → creates a **PENDING** row                                */
    /* ------------------------------------------------------------------ */
    @Transactional
    public void register(Long userId, Long tournamentId) {

        regRepo.findByUser_IdAndTournament_Id(userId, tournamentId)
                .ifPresent(r -> {                     // duplicate request
                    throw new IllegalStateException(
                            "You already have a registration with status " +
                                    r.getStatus());
                });

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Tournament t = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        TournamentUser reg = new TournamentUser(user, t);
        reg.setStatus     (RegistrationStatus.PENDING);
        reg.setRequestedAt(Instant.now());
        regRepo.save(reg);

        notifyAdminsAsync(user, t);      // fire‑and‑forget
    }

    /* ------------------------------------------------------------------ */
    /*  ADMIN actions – keep both overloads                               */
    /* ------------------------------------------------------------------ */

    /** approve by composite key parts (what the UI calls with) */
    @Transactional
    public void approve(Long userId, Long tournamentId) {
        TournamentUser reg = regRepo
                .findByUser_IdAndTournament_Id(userId, tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));
        reg.setStatus(RegistrationStatus.APPROVED);

        // *** new: trigger match creation ***
        User user           = reg.getUser();
        Tournament tournament = reg.getTournament();
        schedulingService.createPairingsForNewPlayer(tournament, user);
    }

    /** approve by primary‑key object (rarely used, still handy) */
    @Transactional
    public void approve(TournamentUserId id) {
        regRepo.findById(id)
                .ifPresent(r -> r.setStatus(RegistrationStatus.APPROVED));
    }

    @Transactional
    public void reject(Long userId, Long tournamentId) {
        TournamentUser reg = regRepo
                .findByUser_IdAndTournament_Id(userId, tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));
        reg.setStatus(RegistrationStatus.REJECTED);
    }

    @Transactional
    public void reject(TournamentUserId id) {
        regRepo.findById(id)
                .ifPresent(r -> r.setStatus(RegistrationStatus.REJECTED));
    }

    public List<TournamentUser> getPending() {
        return regRepo.findByStatus(RegistrationStatus.PENDING);
    }
    public List<TournamentUser> getApproved() {
        return regRepo.findByStatus(RegistrationStatus.APPROVED);
    }

    /* ------------------------------------------------------------------ */

    @Async     // remember to enable @EnableAsync once in a @Configuration class
    protected void notifyAdminsAsync(User user, Tournament t) {
        try {
            List<User> admins = userRepo.findByRole(Role.ADMIN);
            admins.forEach(a -> {
                var m = new org.springframework.mail.SimpleMailMessage();
                m.setTo(a.getEmail());
                m.setSubject("New registration pending");
                m.setText("Player " + user.getUsername() +
                        " wants to join " + t.getName() + ".");
                mailSender.send(m);
            });
        } catch (MailException ex) {
            System.err.println("Mail warning – ignored: " + ex.getMessage());
        }
    }
}
