package com.controller;

import com.config.JwtUtil;
import com.dto.UserRegistrationDTO;
import com.model.User;
import com.model.Role;
import com.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password) {
        // Check if user exists
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("No user exists with username: {}", username);
            return ResponseEntity
                    .badRequest()
                    .body("No user exists with the given username.");
        }

        // If user exists, check the password
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Incorrect password for username: {}", username);
            return ResponseEntity
                    .badRequest()
                    .body("Incorrect password.");
        }

        // Credentials valid → issue JWT
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());
        Map<String, Object> body = Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole().name()
                )
        );

        logger.info("User logged in successfully: {}", username);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO dto,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            logger.warn("Registration validation failed: {}", errorMsg);
            return ResponseEntity.badRequest().body(errorMsg);
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            logger.warn("Password mismatch for user: {}", dto.getUsername());
            return ResponseEntity.badRequest().body("Passwords do not match.");
        }

        if (!isStrongPassword(dto.getPassword())) {
            logger.warn("Weak password provided for user: {}", dto.getUsername());
            return ResponseEntity.badRequest().body(
                    "Password is not strong enough. It must be at least 8 characters long, contain uppercase and lowercase letters, a digit, and a special character."
            );
        }

        User newUser = new User();
        newUser.setFullName(dto.getFullName());
        newUser.setUsername(dto.getUsername());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(dto.getPassword());
        try {
            newUser.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role provided: {}", dto.getRole());
            return ResponseEntity.badRequest().body(
                    "Invalid role. Must be TENNIS_PLAYER, REFEREE, or ADMIN."
            );
        }

        try {
            User savedUser = userService.registerUser(newUser);
            logger.info("User registered successfully: {}", savedUser.getUsername());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("User registration failed for {}: {}", newUser.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean isStrongPassword(String password) {
        String pattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(pattern);
    }
}
