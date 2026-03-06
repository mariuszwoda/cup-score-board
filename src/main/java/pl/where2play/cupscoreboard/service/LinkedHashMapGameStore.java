package pl.where2play.cupscoreboard.service;

import pl.where2play.cupscoreboard.model.Game;
import pl.where2play.cupscoreboard.model.Team;

import java.util.*;

/**
 * {@link LinkedHashMap}-backed implementation of {@link GameStore}.
 *
 * <p>Insertion order is preserved, which provides the tie-breaking information
 * required by {@link InMemoryScoreBoard#getSummary()} — most recently added
 * game appears last in {@link #getAll()}, i.e. highest index.</p>
 *
 * <p>Team names are normalised (trimmed, upper-cased) before use as keys
 * so lookups are case-insensitive.</p>
 *
 * <p>This implementation is <strong>not</strong> thread-safe.</p>
 */
public class LinkedHashMapGameStore implements GameStore {

    private final Map<GameKey, Game> store = new LinkedHashMap<>();

    @Override
    public void save(Game game) {
        store.put(GameKey.of(game.homeTeam(), game.awayTeam()), game);
    }

    @Override
    public Optional<Game> findByTeams(String homeTeam, String awayTeam) {
        return Optional.ofNullable(store.get(GameKey.of(homeTeam, awayTeam)));
    }

    @Override
    public void remove(String homeTeam, String awayTeam) {
        store.remove(GameKey.of(homeTeam, awayTeam));
    }

    @Override
    public List<Game> getAll() {
        return List.copyOf(store.values());
    }

    private record GameKey(String homeTeam, String awayTeam) {

        static GameKey of(Team homeTeam, Team awayTeam) {
            return new GameKey(normalise(homeTeam.name()), normalise(awayTeam.name()));
        }

        static GameKey of(String homeTeam, String awayTeam) {
            return new GameKey(normalise(homeTeam), normalise(awayTeam));
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

