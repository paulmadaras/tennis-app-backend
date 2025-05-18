package com.validation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validates a tennis match score string.
 * <p>
 * Expected format is one to five “set” scores separated by commas, e.g.:
 * <ul>
 *   <li>"6-3"</li>
 *   <li>"6-2, 3-6, 7-6"</li>
 *   <li>"4-6,6-4,10-8"</li>
 * </ul>
 * Each set must be two non‐negative integers separated by a hyphen.
 */
@Component
public class ScoreValidator {

    // matches "number-number", e.g. "6-3", "10-8"
    private static final Pattern SET_PATTERN = Pattern.compile("\\d{1,2}-\\d{1,2}");

    // most matches are best‐of‐5 sets
    private static final int MAX_SETS = 5;

    /**
     * @param score the full match score string, comma‐separated sets
     * @throws IllegalArgumentException if format is invalid
     */
    public void validate(String score) {
        if (score == null || score.isBlank()) {
            throw new IllegalArgumentException("Score cannot be null or blank");
        }

        // split into individual set scores
        String[] sets = score.split(",");
        if (sets.length < 1 || sets.length > MAX_SETS) {
            throw new IllegalArgumentException(
                    "Score must contain between 1 and " + MAX_SETS + " sets (found " + sets.length + ")");
        }

        for (String raw : sets) {
            String set = raw.trim();
            if (!SET_PATTERN.matcher(set).matches()) {
                throw new IllegalArgumentException(
                        "Invalid set format: '" + set + "'. Expected 'x-y' where x and y are numbers");
            }

            String[] parts = set.split("-");
            int p1 = Integer.parseInt(parts[0]);
            int p2 = Integer.parseInt(parts[1]);

            if (p1 < 0 || p2 < 0) {
                throw new IllegalArgumentException(
                        "Set scores must be non-negative: '" + set + "'");
            }

            // Optional: enforce realistic tennis rules, e.g. difference >=2,
            // max games per set, etc. — add here if needed.
        }
    }
}
