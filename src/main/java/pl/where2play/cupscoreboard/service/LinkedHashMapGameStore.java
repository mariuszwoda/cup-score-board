package pl.where2play.cupscoreboard.service;

import pl.where2play.cupscoreboard.model.Game;
import pl.where2play.cupscoreboard.model.Team;

import java.util.*;

/**
 * {@link LinkedHashMap}-backed implementation of {@link GameStore}.
 *
 * <p>Insertion order is preserved. {@link InMemoryScoreBoard#getSummary()} uses
 * {@link pl.where2play.cupscoreboard.model.Game#startedAt()} for tie-breaking,
 * so insertion order in the store is not relied upon for ordering.</p>
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
    public boolean remove(String homeTeam, String awayTeam) {
        return store.remove(GameKey.of(homeTeam, awayTeam)) != null;
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

