package com.service;

import com.model.User;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.validation.PasswordValidator;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordValidator passwordValidator;  // <<

    public User registerUser(User user) {
        // Validate unique username and email
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if(existingUser.isPresent()){
            throw new IllegalArgumentException("Username already exists.");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists.");
        }

        // 2) Password strength
        passwordValidator.validate(user.getPassword());

        // 3) Encode + save
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Add this method to delegate to the UserRepository
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // For login, you would typically verify the password against the encoded one
    public Optional<User> login(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword())){
            return userOpt;
        }
        return Optional.empty();
    }

    /** Needed by the controller to fetch for the settings form */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Update only fullName, email, and (if provided) password.
     */
    public User updateUser(Long id, String fullName, String email, String rawPassword) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        u.setFullName(fullName);
        u.setEmail(email);

        if (rawPassword != null && !rawPassword.isBlank()) {
            // Validate strength
            passwordValidator.validate(rawPassword);

            // Then encode and set
            u.setPassword(passwordEncoder.encode(rawPassword));
        }

        return userRepository.save(u);
    }

    /**
     * Return all users in the system.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
