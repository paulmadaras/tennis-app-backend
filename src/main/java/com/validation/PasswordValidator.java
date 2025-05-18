package com.validation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    // At least 8 chars, one upper, one lower, one digit, one special
    private static final Pattern VALID_PASSWORD =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\p{Punct}).{8,}$");

    public void validate(String rawPassword) {
        if (rawPassword == null || !VALID_PASSWORD.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and include uppercase, "
                            + "lowercase, digit, and special character");
        }
    }
}
