package pl.where2play.cupscoreboard.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable entity representing a live football match.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class Game {

    private static final String TO_STRING_FORMAT = "%s %d - %s %d";
    private static final String MUST_NOT_BE_NULL = "must not be null";
    private static final String SCORE_MUST_NOT_BE_NULL = "Score " + MUST_NOT_BE_NULL;

    @EqualsAndHashCode.Include
    Team homeTeam;

    @EqualsAndHashCode.Include
    Team awayTeam;

    Score score;
    Instant startedAt;

    /**
     * Creates a new {@link Game} with {@link Score#ZERO} and the current time as start time.
     */
    public Game(Team homeTeam, Team awayTeam) {
        this(homeTeam, awayTeam, Score.ZERO, Instant.now());
    }

    /**
     * Full constructor used internally to produce updated copies via {@link #withScore(Score)}.
     */
    private Game(Team homeTeam, Team awayTeam, Score score, Instant startedAt) {
        this.homeTeam = Objects.requireNonNull(homeTeam, "Home team " + MUST_NOT_BE_NULL);
        this.awayTeam = Objects.requireNonNull(awayTeam, "Away team " + MUST_NOT_BE_NULL);
        this.score = Objects.requireNonNull(score, SCORE_MUST_NOT_BE_NULL);
        this.startedAt = Objects.requireNonNull(startedAt, "Start time " + MUST_NOT_BE_NULL);
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Home and away teams must be different");
        }
    }

    /**
     * Returns a new {@link Game} with the given score, preserving all other fields.
     */
    public Game withScore(Score newScore) {
        Objects.requireNonNull(newScore, SCORE_MUST_NOT_BE_NULL);
        return new Game(homeTeam, awayTeam, newScore, startedAt);
    }

    /**
     * Returns a string representation in the format: {@code "HomeTeam homeScore - AwayTeam awayScore"}.
     *
     * @return formatted game state string
     */
    @Override
    public String toString() {
        return TO_STRING_FORMAT.formatted(
                homeTeam.name(), score.home(),
                awayTeam.name(), score.away());
    }
}
