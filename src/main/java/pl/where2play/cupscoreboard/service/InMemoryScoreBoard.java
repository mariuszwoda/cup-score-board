package pl.where2play.cupscoreboard.service;

import pl.where2play.cupscoreboard.exception.GameAlreadyExistsException;
import pl.where2play.cupscoreboard.model.Game;
import pl.where2play.cupscoreboard.model.Team;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory implementation of {@link ScoreBoard}.
 *
 * <p>Games are stored in a {@link LinkedHashMap} to preserve insertion order,
 * which is used as a tie-breaker when two games share the same total score —
 * the most recently started game appears first in the summary.
 */
public class InMemoryScoreBoard implements ScoreBoard {

    private final Map<GameKey, Game> games = new LinkedHashMap<>();

    @Override
    public Game startGame(String homeTeam, String awayTeam) {
        GameKey key = GameKey.of(homeTeam, awayTeam);
        if (games.containsKey(key)) {
            throw new GameAlreadyExistsException(homeTeam, awayTeam);
        }
        Game game = new Game(new Team(homeTeam), new Team(awayTeam));
        games.put(key, game);
        return game;
    }

    @Override
    public void finishGame(String homeTeam, String awayTeam) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Game updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<Game> getSummary() {
        return List.copyOf(games.values());
    }

    private record GameKey(String homeTeam, String awayTeam) {

        GameKey {
            homeTeam = normalise(homeTeam);
            awayTeam = normalise(awayTeam);
        }

        /**
         * Factory method providing a named, readable construction point.
         */
        static GameKey of(String homeTeam, String awayTeam) {
            return new GameKey(homeTeam, awayTeam);
        }

        private static String normalise(String name) {
            Objects.requireNonNull(name, "Team name must not be null");
            String trimmed = name.trim();
            if (trimmed.isBlank()) {
                throw new IllegalArgumentException("Team name must not be blank");
            }
            return trimmed.toUpperCase();
        }
    }
}
