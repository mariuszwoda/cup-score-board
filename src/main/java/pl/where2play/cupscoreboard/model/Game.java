package pl.where2play.cupscoreboard.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable entity representing a live football match.
 */
public final class Game {

    private final Team homeTeam;
    private final Team awayTeam;
    private final Score score;
    private final Instant startedAt;

    public Game(Team homeTeam, Team awayTeam) {
        this(homeTeam, awayTeam, Score.ZERO, Instant.now());
    }

    private Game(Team homeTeam, Team awayTeam, Score score, Instant startedAt) {
        Objects.requireNonNull(homeTeam, "Home team must not be null");
        Objects.requireNonNull(awayTeam, "Away team must not be null");
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Home and away teams must be different");
        }
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.score = score;
        this.startedAt = startedAt;
    }

    public Team homeTeam() {
        return homeTeam;
    }

    public Team awayTeam() {
        return awayTeam;
    }

    public Score score() {
        return score;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public Game withScore(Score newScore) {
        Objects.requireNonNull(newScore, "Score must not be null");
        return new Game(homeTeam, awayTeam, newScore, startedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game game)) return false;
        return homeTeam.equals(game.homeTeam) && awayTeam.equals(game.awayTeam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTeam, awayTeam);
    }

    @Override
    public String toString() {
        return "%s %d - %s %d".formatted(
                homeTeam.name(), score.home(),
                awayTeam.name(), score.away());
    }
}
