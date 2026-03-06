package pl.where2play.cupscoreboard.service;

import pl.where2play.cupscoreboard.exception.GameAlreadyExistsException;
import pl.where2play.cupscoreboard.exception.GameNotFoundException;
import pl.where2play.cupscoreboard.model.Game;

import java.util.List;

/**
 * Contract for a live football scoreboard.
 *
 * <p>Implementations must be consistent with the following rules:
 * <ul>
 *   <li>All team-name comparisons are case-insensitive and trim surrounding whitespace.</li>
 *   <li>A game is uniquely identified by the combination of {@code homeTeam} and {@code awayTeam}.</li>
 *   <li>The same team may not appear as both home and away in the same game.</li>
 * </ul>
 *
 * @see Game
 * @see GameAlreadyExistsException
 * @see GameNotFoundException
 * @since 1.0
 */
public interface ScoreBoard {

    /**
     * Starts a new game with an initial score of 0–0 and adds it to the live board.
     *
     * @param homeTeam name of the home team; must not be {@code null} or blank
     * @param awayTeam name of the away team; must not be {@code null}, blank, or equal to {@code homeTeam}
     * @return the newly created {@link Game} with {@link pl.where2play.cupscoreboard.model.Score#ZERO}
     * @throws NullPointerException       if {@code homeTeam} or {@code awayTeam} is {@code null}
     * @throws IllegalArgumentException   if {@code homeTeam} or {@code awayTeam} is blank,
     *                                    or if both teams are the same
     * @throws GameAlreadyExistsException if a game between these two teams is already live
     */
    Game startGame(String homeTeam, String awayTeam);

    /**
     * Finishes a live game and removes it from the board.
     *
     * <p>After this call, the game is no longer available via {@link #getSummary()}.
     *
     * @param homeTeam name of the home team; must not be {@code null} or blank
     * @param awayTeam name of the away team; must not be {@code null} or blank
     * @throws NullPointerException     if {@code homeTeam} or {@code awayTeam} is {@code null}
     * @throws IllegalArgumentException if {@code homeTeam} or {@code awayTeam} is blank
     * @throws GameNotFoundException    if no live game exists for the given team pair
     */
    void finishGame(String homeTeam, String awayTeam);

    /**
     * Updates the absolute score of a live game.
     *
     * <p>Scores are absolute, not incremental — passing {@code homeScore = 3} sets
     * the home score to 3 regardless of the current value.
     *
     * @param homeTeam  name of the home team; must not be {@code null} or blank
     * @param awayTeam  name of the away team; must not be {@code null} or blank
     * @param homeScore new absolute home-team score; must be &gt;= 0
     * @param awayScore new absolute away-team score; must be &gt;= 0
     * @return the updated {@link Game} reflecting the new score
     * @throws NullPointerException     if {@code homeTeam} or {@code awayTeam} is {@code null}
     * @throws IllegalArgumentException if any score value is negative,
     *                                  or if {@code homeTeam} or {@code awayTeam} is blank
     * @throws GameNotFoundException    if no live game exists for the given team pair
     */
    Game updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore);

    /**
     * Returns an immutable snapshot of all live games ordered by:
     * <ol>
     *   <li>Total goals (home + away) — descending</li>
     *   <li>Start time — most recently started first (for ties in total goals)</li>
     * </ol>
     *
     * <p>The returned list reflects the state of the board at the moment of the call.
     * Subsequent modifications to the board are not reflected in the returned list.
     *
     * @return an unmodifiable, ordered {@link List} of live {@link Game} instances;
     * never {@code null}, empty if no games are currently live
     */
    List<Game> getSummary();
}
