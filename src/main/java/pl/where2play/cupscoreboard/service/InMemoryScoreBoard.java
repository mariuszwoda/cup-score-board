package pl.where2play.cupscoreboard.service;

import pl.where2play.cupscoreboard.exception.GameAlreadyExistsException;
import pl.where2play.cupscoreboard.exception.GameNotFoundException;
import pl.where2play.cupscoreboard.model.Game;
import pl.where2play.cupscoreboard.model.Score;
import pl.where2play.cupscoreboard.model.Team;

import java.util.*;

/**
 * In-memory implementation of {@link ScoreBoard}.
 *
 * <p>Delegates all storage operations to a {@link GameStore}, defaulting to
 * {@link LinkedHashMapGameStore}. The store preserves insertion order which
 * provides the tie-breaking needed by {@link #getSummary()}.</p>
 *
 * <p>This implementation is <strong>not</strong> thread-safe.</p>
 */
public class InMemoryScoreBoard implements ScoreBoard {

    private final GameStore store;

    /**
     * Creates a board backed by the default {@link LinkedHashMapGameStore}.
     */
    public InMemoryScoreBoard() {
        this(new LinkedHashMapGameStore());
    }

    /**
     * Creates a board backed by the provided {@link GameStore} (for testing or custom stores).
     */
    public InMemoryScoreBoard(GameStore store) {
        this.store = store;
    }

    @Override
    public Game startGame(String homeTeam, String awayTeam) {
        if (store.findByTeams(homeTeam, awayTeam).isPresent()) {
            throw new GameAlreadyExistsException(homeTeam, awayTeam);
        }
        Game game = new Game(new Team(homeTeam), new Team(awayTeam));
        store.save(game);
        return game;
    }

    @Override
    public void finishGame(String homeTeam, String awayTeam) {
        store.findByTeams(homeTeam, awayTeam)
                .orElseThrow(() -> new GameNotFoundException(homeTeam, awayTeam));
        store.remove(homeTeam, awayTeam);
    }

    @Override
    public Game updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        Game existing = store.findByTeams(homeTeam, awayTeam)
                .orElseThrow(() -> new GameNotFoundException(homeTeam, awayTeam));
        Game updated = existing.withScore(new Score(homeScore, awayScore));
        store.save(updated);
        return updated;
    }

    @Override
    public List<Game> getSummary() {
        List<Game> all = store.getAll();
        return all.stream()
                .sorted(Comparator
                        .comparingInt((Game g) -> g.score().totalGoals())
                        .reversed()
                        .thenComparingInt(g -> -all.indexOf(g)))
                .toList();
    }
}
