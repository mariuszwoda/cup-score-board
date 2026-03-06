package pl.where2play.cupscoreboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.where2play.cupscoreboard.model.Game;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Tag("unit")
@DisplayName("ScoreBoard.getSummary()")
class GetSummaryTest {

    // -------------------------------------------------------------------------
    // Team name constants
    // -------------------------------------------------------------------------

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
    private static final String TEAM_A = "Team A";
    private static final String TEAM_B = "Team B";
    private static final String TEAM_C = "Team C";
    private static final String TEAM_D = "Team D";

    private ScoreBoard scoreBoard;

    @BeforeEach
    void setUp() {
        // Arrange (shared) — fresh board for every test
        scoreBoard = new InMemoryScoreBoard();
    }

    // -------------------------------------------------------------------------
    // Empty / initial state
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when no games are active")
    class WhenNoGamesAreActive {

        @Test
        @DisplayName("should return an empty list")
        void shouldReturnEmptyListWhenNoGamesAreActive() {
            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert
            assertThat(summary).isEmpty();
        }

        @Test
        @DisplayName("should return an empty list after all games are finished")
        void shouldReturnEmptyListWhenAllGamesFinished() {
            // Arrange
            scoreBoard.startGame(MEXICO, CANADA);
            scoreBoard.finishGame(MEXICO, CANADA);

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert
            assertThat(summary).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // List contract
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when checking list contract")
    class WhenCheckingListContract {

        @Test
        @DisplayName("should return an unmodifiable list")
        void shouldReturnUnmodifiableList() {
            // Arrange
            scoreBoard.startGame(MEXICO, CANADA);
            List<Game> summary = scoreBoard.getSummary();

            // Act
            Throwable thrown = catchThrowable(() -> summary.add(null));

            // Assert
            assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should return a single-entry summary for one active game with correct identity and score")
        void shouldReturnSingleGameSummary() {
            // Arrange
            scoreBoard.startGame(MEXICO, CANADA);
            scoreBoard.updateScore(MEXICO, CANADA, 2, 5);

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert
            assertThat(summary).hasSize(1);
            assertThat(summary.getFirst()).satisfies(game -> assertSoftly(softly -> {
                softly.assertThat(game.homeTeam().name()).isEqualTo(MEXICO);
                softly.assertThat(game.awayTeam().name()).isEqualTo(CANADA);
                softly.assertThat(game.score().home()).isEqualTo(2);
                softly.assertThat(game.score().away()).isEqualTo(5);
            }));
        }
    }

    // -------------------------------------------------------------------------
    // Ordering
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when ordering games")
    class WhenOrderingGames {

        @Test
        @DisplayName("should order games by total score descending")
        void shouldOrderGamesByTotalScoreDescending() {
            // Arrange — totals: Mexico/Canada=5, Spain/Brazil=12, Germany/France=4,
            //                   Uruguay/Italy=12 (more recent), Argentina/Australia=4 (more recent)
            scoreBoard.startGame(MEXICO, CANADA);
            scoreBoard.updateScore(MEXICO, CANADA, 0, 5);

            scoreBoard.startGame(SPAIN, BRAZIL);
            scoreBoard.updateScore(SPAIN, BRAZIL, 10, 2);

            scoreBoard.startGame(GERMANY, FRANCE);
            scoreBoard.updateScore(GERMANY, FRANCE, 2, 2);

            scoreBoard.startGame(URUGUAY, ITALY);
            scoreBoard.updateScore(URUGUAY, ITALY, 6, 6);

            scoreBoard.startGame(ARGENTINA, AUSTRALIA);
            scoreBoard.updateScore(ARGENTINA, AUSTRALIA, 3, 1);

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert — expected: Uruguay(12), Spain(12 older), Mexico(5), Argentina(4 newer), Germany(4)
            assertThat(summary)
                    .hasSize(5)
                    .extracting(game -> game.homeTeam().name())
                    .containsExactly(URUGUAY, SPAIN, MEXICO, ARGENTINA, GERMANY);
        }

        @Test
        @DisplayName("should break ties by most recently started game first")
        void shouldBreakTiesByMostRecentlyAddedFirst() {
            // Arrange — both games have total score of 2; Team C/D started more recently
            scoreBoard.startGame(TEAM_A, TEAM_B);
            scoreBoard.updateScore(TEAM_A, TEAM_B, 1, 1);

            scoreBoard.startGame(TEAM_C, TEAM_D);
            scoreBoard.updateScore(TEAM_C, TEAM_D, 1, 1);

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert
            assertThat(summary)
                    .hasSize(2)
                    .extracting(game -> game.homeTeam().name())
                    .containsExactly(TEAM_C, TEAM_A);
        }
    }

    // -------------------------------------------------------------------------
    // Finished games
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when games are finished")
    class WhenGamesAreFinished {

        @Test
        @DisplayName("should not include finished game and preserve remaining game identity")
        void shouldNotIncludeFinishedGamesInSummary() {
            // Arrange
            scoreBoard.startGame(MEXICO, CANADA);
            scoreBoard.updateScore(MEXICO, CANADA, 0, 5);

            scoreBoard.startGame(SPAIN, BRAZIL);
            scoreBoard.updateScore(SPAIN, BRAZIL, 10, 2);

            scoreBoard.finishGame(MEXICO, CANADA);

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert
            assertThat(summary)
                    .hasSize(1)
                    .first()
                    .satisfies(game -> assertSoftly(softly -> {
                        softly.assertThat(game.homeTeam().name()).isEqualTo(SPAIN);
                        softly.assertThat(game.awayTeam().name()).isEqualTo(BRAZIL);
                    }));
        }

        @Test
        @DisplayName("should not include the most recently started game when it is finished")
        void shouldNotIncludeMostRecentGameWhenFinished() {
            // Arrange
            scoreBoard.startGame(MEXICO, CANADA);
            scoreBoard.updateScore(MEXICO, CANADA, 1, 1);

            scoreBoard.startGame(SPAIN, BRAZIL);
            scoreBoard.updateScore(SPAIN, BRAZIL, 1, 1);

            scoreBoard.finishGame(SPAIN, BRAZIL);

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert
            assertThat(summary)
                    .hasSize(1)
                    .first()
                    .satisfies(game -> {
                        assertThat(game.homeTeam().name()).isEqualTo(MEXICO);
                        assertThat(game.awayTeam().name()).isEqualTo(CANADA);
                    });
        }

        @Test
        @DisplayName("should return empty list when all games are finished")
        void shouldReturnEmptyWhenAllGamesFinished() {
            // Arrange
            scoreBoard.startGame(MEXICO, CANADA);
            scoreBoard.startGame(SPAIN, BRAZIL);
            scoreBoard.finishGame(MEXICO, CANADA);
            scoreBoard.finishGame(SPAIN, BRAZIL);

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert
            assertThat(summary).isEmpty();
        }
    }
}
