// src/test/java/com/service/UserServiceIntegrationTest.java
package com.service;

import com.model.Role;
import com.model.User;
import com.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @TestConfiguration
    static class StubMailConfig {
        @Bean
        public JavaMailSender mailSender() {
            return new JavaMailSenderImpl();  // defaults are fine for tests
        }
    }

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void registerNewUser_persistsAndEncodesPassword() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: A brand-new user signs up.
        // Expectation:
        //   1) The user is saved and assigned a non-null ID.
        //   2) The raw password is stored in encoded form (and matches when checked).
        // ────────────────────────────────────────────────────────────────────────────

        // 1) Build a new User with a clear-text password
        var u = new User("alice123", "alice@example.com", "MyP@ssw0rd",
                Role.TENNIS_PLAYER, "Alice Smith");

        // 2) Register the user
        var saved = userService.registerUser(u);

        // 3) Assert that an ID was generated (i.e. it was persisted)
        assertNotNull(saved.getId());

        // 4) Assert that the stored password matches the raw one when decoded
        assertTrue(passwordEncoder.matches("MyP@ssw0rd", saved.getPassword()));

        // Note: @Transactional on the test class rolls back after each test.
    }

    @Test
    void register_duplicateUsername_throws() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: Two users attempt to register with the same username.
        // Expectation: The second registration call throws an IllegalArgumentException
        //              with message "Username already exists."
        // ────────────────────────────────────────────────────────────────────────────

        // 1) First user registers successfully
        var u1 = new User("bob", "bob1@example.com", "Secret1!",
                Role.TENNIS_PLAYER, "Bob One");
        userService.registerUser(u1);

        // 2) Second user with the same username but different email
        var u2 = new User("bob", "bob2@example.com", "Secret2!",
                Role.TENNIS_PLAYER, "Bob Two");

        // 3) Expect an exception on duplicate username
        var ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(u2));
        assertEquals("Username already exists.", ex.getMessage());
    }

    @Test
    void register_duplicateEmail_throws() {
        // ────────────────────────────────────────────────────────────────────────────
        // Scenario: Two users attempt to register with the same email.
        // Expectation: The second registration call throws an IllegalArgumentException
        //              with message "Email already exists."
        // ────────────────────────────────────────────────────────────────────────────

        // 1) First user registers successfully
        var u1 = new User("charlie1", "charlie@example.com", "Secret1!",
                Role.TENNIS_PLAYER, "Charlie One");
        userService.registerUser(u1);

        // 2) Second user with a different username but same email
        var u2 = new User("charlie2", "charlie@example.com", "Secret2!",
                Role.TENNIS_PLAYER, "Charlie Two");

        // 3) Expect an exception on duplicate email
        var ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(u2));
        assertEquals("Email already exists.", ex.getMessage());
    }

}
