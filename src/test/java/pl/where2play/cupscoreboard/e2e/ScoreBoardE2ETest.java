package pl.where2play.cupscoreboard.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.where2play.cupscoreboard.model.Game;
import pl.where2play.cupscoreboard.service.InMemoryScoreBoard;
import pl.where2play.cupscoreboard.service.ScoreBoard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * End-to-end scenario.
 *
 * <pre>
 * Active games:
 *   Mexico    0 – Canada    5
 *   Spain    10 – Brazil    2
 *   Germany   2 – France    2
 *   Uruguay   6 – Italy     6
 *   Argentina 3 – Australia 1
 *
 * Expected summary order:
 *   1. Uruguay   6 – Italy     6   (12 goals, most recent)
 *   2. Spain    10 – Brazil    2   (12 goals)
 *   3. Mexico    0 – Canada    5   (5 goals)
 *   4. Argentina 3 – Australia 1   (4 goals, most recent)
 *   5. Germany   2 – France    2   (4 goals)
 * </pre>
 *
 * @see ScoreBoard
 * @see InMemoryScoreBoard
 */
@DisplayName("ScoreBoard EndToEnd scenario")
class ScoreBoardE2ETest {

    private static final String MEXICO = "Mexico";
    private static final String CANADA = "Canada";
    private static final String SPAIN = "Spain";
    private static final String BRAZIL = "Brazil";
    private static final String GERMANY = "Germany";
    private static final String FRANCE = "France";
    private static final String URUGUAY = "Uruguay";
    private static final String ITALY = "Italy";
    private static final String ARGENTINA = "Argentina";
    private static final String AUSTRALIA = "Australia";

    @Test
    @DisplayName("should produce correct summary order for the specification example")
    void shouldProduceCorrectSummaryForSpecificationExample() {
        // Arrange
        List<Game> summary = getGames();

        // Assert
        assertThat(summary)
                .hasSize(5)
                .extracting(
                        game -> game.homeTeam().name(),
                        game -> game.awayTeam().name(),
                        game -> game.score().home(),
                        game -> game.score().away()
                )
                .containsExactly(
                        tuple(URUGUAY, ITALY, 6, 6),
                        tuple(SPAIN, BRAZIL, 10, 2),
                        tuple(MEXICO, CANADA, 0, 5),
                        tuple(ARGENTINA, AUSTRALIA, 3, 1),
                        tuple(GERMANY, FRANCE, 2, 2)
                );
    }

    private List<Game> getGames() {
        ScoreBoard board = new InMemoryScoreBoard();

        board.startGame(MEXICO, CANADA);
        board.startGame(SPAIN, BRAZIL);
        board.startGame(GERMANY, FRANCE);
        board.startGame(URUGUAY, ITALY);
        board.startGame(ARGENTINA, AUSTRALIA);

        board.updateScore(MEXICO, CANADA, 0, 5);        // total: 5
        board.updateScore(SPAIN, BRAZIL, 10, 2);        // total: 12
        board.updateScore(GERMANY, FRANCE, 2, 2);       // total: 4
        board.updateScore(URUGUAY, ITALY, 6, 6);        // total: 12 (more recent than Spain)
        board.updateScore(ARGENTINA, AUSTRALIA, 3, 1);  // total: 4  (more recent than Germany)

        // Act
        return board.getSummary();
    }

    @Test
    @DisplayName("should list all five games as active before any scores are updated")
    void shouldListAllGamesAsActiveWithZeroScores() {
        // Arrange
        ScoreBoard board = new InMemoryScoreBoard();

        board.startGame(MEXICO, CANADA);
        board.startGame(SPAIN, BRAZIL);
        board.startGame(GERMANY, FRANCE);
        board.startGame(URUGUAY, ITALY);
        board.startGame(ARGENTINA, AUSTRALIA);

        // Act
        List<Game> summary = board.getSummary();

        // Assert — all five games present, all at 0-0, order is insertion order (all tied at 0)
        assertThat(summary)
                .hasSize(5)
                .extracting(game -> game.score().totalGoals())
                .containsOnly(0);

        assertThat(summary)
                .extracting(game -> game.homeTeam().name())
                .containsExactly(ARGENTINA, URUGUAY, GERMANY, SPAIN, MEXICO);
    }
}
