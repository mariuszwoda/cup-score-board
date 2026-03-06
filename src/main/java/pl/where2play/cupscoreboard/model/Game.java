package pl.where2play.cupscoreboard.model;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.Instant;

/**
 * Immutable entity representing a live football match.
 */
@Getter
@Accessors(fluent = true)
public final class Game {
    Team homeTeam;
    Team awayTeam;
    Score score;
    Instant startedAt;

    public Game(Team homeTeam, Team awayTeam) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    private Game(Team homeTeam, Team awayTeam, Score score, Instant startedAt) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public Game withScore(Score newScore) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
