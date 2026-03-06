package pl.where2play.cupscoreboard.service;

import pl.where2play.cupscoreboard.model.Game;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction over the in-memory storage of live games.
 *
 * <p>Decouples {@link InMemoryScoreBoard} from any specific data structure,
 * allowing the storage strategy to be replaced without touching board logic.</p>
 */
public interface GameStore {

    /**
     * Persists or replaces a game (matched by home+away team identity).
     */
    void save(Game game);

    /**
     * Finds a live game by home and away team name (case-insensitive, trimmed).
     */
    Optional<Game> findByTeams(String homeTeam, String awayTeam);

    /**
     * Removes the game identified by the given team names.
     */
    void remove(String homeTeam, String awayTeam);

    /**
     * Returns all live games in insertion order (oldest first).
     */
    List<Game> getAll();
}

