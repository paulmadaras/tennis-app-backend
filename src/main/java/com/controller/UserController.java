// src/main/java/com/controller/UserController.java
package com.controller;

import com.dto.UserDTO;
import com.dto.UpdateUserRequest;
import com.model.User;
import com.repository.UserRepository;
import com.repository.TournamentUserRepository;
import com.repository.MatchRepository;
import com.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final TournamentUserRepository tournamentUserRepository;
    private final MatchRepository matchRepository;
    private final UserService userService;

    public UserController(
            UserRepository userRepository,
            TournamentUserRepository tournamentUserRepository,
            MatchRepository matchRepository,
            UserService userService
    ) {
        this.userRepository            = userRepository;
        this.tournamentUserRepository  = tournamentUserRepository;
        this.matchRepository           = matchRepository;
        this.userService               = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            logger.warn("Delete: user {} not found", id);
            return ResponseEntity.notFound().build();
        }

        // 1) remove tournament registrations
        tournamentUserRepository.deleteByUser_Id(id);

        // 2) remove matches where user participated
        matchRepository.deleteByPlayer1IdOrPlayer2Id(id, id);

        // 3) remove matches where user was referee
        matchRepository.deleteByRefereeId(id);

        // 4) finally delete the user
        userRepository.deleteById(id);
        logger.info("Deleted user {} and all related data", id);

        return ResponseEntity.ok("User deleted");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> {
                    UserDTO dto = new UserDTO(u.getId(), u.getFullName(), u.getEmail());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserById(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest req
    ) {
        User updated = userService.updateUser(
                id,
                req.getFullName(),
                req.getEmail(),
                req.getPassword()
        );
        UserDTO dto = new UserDTO(updated.getId(), updated.getFullName(), updated.getEmail());
        return ResponseEntity.ok(dto);
    }
}
