package pl.where2play.cupscoreboard.service;

import pl.where2play.cupscoreboard.exception.GameAlreadyExistsException;
import pl.where2play.cupscoreboard.exception.GameNotFoundException;
import pl.where2play.cupscoreboard.model.Game;

import java.util.List;
/*
Instructions:

Please provide the implementation of the Football World Cup Score Board as a simple
library.

Guidelines:

• Keep it simple. Stick to the requirements and try to implement the simplest
solution you can possibly think of that works and don't forget about edge cases.
• Use an in-memory store solution (for example just use collections to store
the information you might require).
• We are NOT looking for a REST API, a Web Service or Microservice. Just
a simple implementation.
• Focus on Quality. Use Test-Driven Development (TDD), pay attention to
OO design, Clean Code and adherence to SOLID principles.
• Approach. Code the solution according to your standards. Please share your
solution with a link to a source control repository (e.g. GitHub, GitLab,
BitBucket) as we would like you to see your progress (your commit history is
important)
• Add a README.md file where you can make notes of any assumption or
things you would like to mention to us about your solution.
• If the implementation is in a frontend language, then it must follow all of the
above guidelines and additionally you should apply the suggestions below:
o If it is written it in a specific UI framework or library then we would
suggest writing the simplest component/s to serve the described
functionality. Please don’t spend time making it look good.
o If it is written in plain JavaScript then we would suggest
implementing the solution as a simple service or module.

Football World Cup Score Board:

You are working on a sports data company. And we would like you to develop a new
Live Football World Cup Score Board that shows matches and scores.
The boards support the following operations:
1. Start a game. When a game starts, it should capture (being initial score 0-0)
a. Home team
b. Away Team
2. Finish a game. It will remove a match from the scoreboard.
3. Update score. Receiving the pair score; home team score and away team score
updates a game score
4. Get a summary of games by total score. Those games with the same total score
will be returned ordered by the most recently added to our system.

As an example, being the current data in the system:
a. Mexico - Canada: 0 – 5
b. Spain - Brazil: 10 – 2
c. Germany - France: 2 – 2
d. Uruguay - Italy: 6 – 6
e. Argentina - Australia: 3 - 1

The summary would provide with the following information:
1. Uruguay 6 - Italy 6
2. Spain 10 - Brazil 2
3. Mexico 0 - Canada 5
4. Argentina 3 - Australia 1
5. Germany 2 - France 2
Thank you for your time to complete the exercise!
 */

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
