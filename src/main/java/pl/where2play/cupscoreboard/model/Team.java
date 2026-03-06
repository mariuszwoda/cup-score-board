package pl.where2play.cupscoreboard.model;

import java.util.Objects;

/**
 * Immutable value object representing a football team identified by its name.
 */
public record Team(String name) {
    public Team {
        Objects.requireNonNull(name, "Team name must not be null");
        name = name.trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Team name must not be blank");
        }
    }
}
