package pl.where2play.cupscoreboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.where2play.cupscoreboard.exception.GameNotFoundException;
import pl.where2play.cupscoreboard.model.Game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Tag("unit")
@DisplayName("ScoreBoard.updateScore()")
class UpdateScoreTest {

    private static final String HOME_TEAM = "Spain";
    private static final String AWAY_TEAM = "Brazil";
    private static final String UNKNOWN_HOME = "Germany";
    private static final String UNKNOWN_AWAY = "France";
    private static final String TEAM_NAME_NULL_MESSAGE = "Team name must not be null";
    private static final String TEAM_NAME_BLANK_MESSAGE = "Team name must not be blank";
    private static final String SCORE_NEGATIVE_MESSAGE = "Score values must not be negative";

    private ScoreBoard scoreBoard;

    @BeforeEach
    void setUp() {
        // Arrange (shared) — fresh board with one live game for every test
        scoreBoard = new InMemoryScoreBoard();
        scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);
    }

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when updating score for an existing game")
    class WhenUpdatingExistingGame {

        @Test
        @DisplayName("should return game with updated home and away scores")
        void shouldUpdateScoreForExistingGame() {
            // Act
            Game updated = scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 10, 2);

            // Assert
            assertSoftly(softly -> {
                softly.assertThat(updated.score().home()).isEqualTo(10);
                softly.assertThat(updated.score().away()).isEqualTo(2);
            });
        }

        @Test
        @DisplayName("should reflect updated score and correct game identity in summary")
        void shouldReflectUpdatedScoreInSummary() {
            // Act
            Game updated = scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 10, 2);

            // Assert
            assertThat(scoreBoard.getSummary())
                    .hasSize(1)
                    .first()
                    .satisfies(game -> assertSoftly(softly -> {
                        softly.assertThat(game.homeTeam().name()).isEqualTo(HOME_TEAM);
                        softly.assertThat(game.awayTeam().name()).isEqualTo(AWAY_TEAM);
                        softly.assertThat(game.score()).isEqualTo(updated.score());
                    }));
        }

        @Test
        @DisplayName("should allow score correction downward")
        void shouldAllowScoreCorrectionDownward() {
            // Arrange
            scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 5, 3);

            // Act
            Game corrected = scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 4, 3);

            // Assert
            assertSoftly(softly -> {
                softly.assertThat(corrected.score().home()).isEqualTo(4);
                softly.assertThat(corrected.score().away()).isEqualTo(3);
            });
        }

        @Test
        @DisplayName("should allow score correction to zero-zero")
        void shouldAllowScoreCorrectionToZero() {
            // Arrange
            scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 3, 2);

            // Act
            Game corrected = scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 0, 0);

            // Assert
            assertSoftly(softly -> {
                softly.assertThat(corrected.score().home()).isZero();
                softly.assertThat(corrected.score().away()).isZero();
            });
        }

        @Test
        @DisplayName("should be case-insensitive for team names")
        void shouldBeCaseInsensitiveForTeamNames() {
            // Act
            Game updated = scoreBoard.updateScore(HOME_TEAM.toLowerCase(), AWAY_TEAM.toUpperCase(), 3, 1);

            // Assert
            assertSoftly(softly -> {
                softly.assertThat(updated.score().home()).isEqualTo(3);
                softly.assertThat(updated.score().away()).isEqualTo(1);
            });
        }
    }

    // -------------------------------------------------------------------------
    // Game not found
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when game does not exist")
    class WhenGameDoesNotExist {

        @Test
        @DisplayName("should throw GameNotFoundException for unknown fixture")
        void shouldThrowWhenGameDoesNotExist() {
            // Act
            Throwable thrown = catchThrowable(() ->
                    scoreBoard.updateScore(UNKNOWN_HOME, UNKNOWN_AWAY, 1, 0));

            // Assert
            assertThat(thrown).isInstanceOf(GameNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // Negative scores
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when score values are negative")
    class WhenScoreIsNegative {

        @Test
        @DisplayName("should throw IllegalArgumentException with message when home score is negative")
        void shouldThrowWhenHomeScoreIsNegative() {
            // Act
            Throwable thrown = catchThrowable(() ->
                    scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, -1, 0));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(SCORE_NEGATIVE_MESSAGE);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with message when away score is negative")
        void shouldThrowWhenAwayScoreIsNegative() {
            // Act
            Throwable thrown = catchThrowable(() ->
                    scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 0, -1));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(SCORE_NEGATIVE_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // Null inputs
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when team name is null")
    class WhenTeamNameIsNull {

        @Test
        @DisplayName("should throw NullPointerException when home team is null")
        void shouldThrowWhenHomeTeamIsNull() {
            // Act
            Throwable thrown = catchThrowable(() ->
                    scoreBoard.updateScore(null, AWAY_TEAM, 1, 0));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining(TEAM_NAME_NULL_MESSAGE);
        }

        @Test
        @DisplayName("should throw NullPointerException when away team is null")
        void shouldThrowWhenAwayTeamIsNull() {
            // Act
            Throwable thrown = catchThrowable(() ->
                    scoreBoard.updateScore(HOME_TEAM, null, 1, 0));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining(TEAM_NAME_NULL_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // Blank inputs
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when team name is blank")
    class WhenTeamNameIsBlank {

        @Test
        @DisplayName("should throw IllegalArgumentException when home team is blank")
        void shouldThrowWhenHomeTeamIsBlank() {
            // Act
            Throwable thrown = catchThrowable(() ->
                    scoreBoard.updateScore("  ", AWAY_TEAM, 1, 0));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(TEAM_NAME_BLANK_MESSAGE);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when away team is blank")
        void shouldThrowWhenAwayTeamIsBlank() {
            // Act
            Throwable thrown = catchThrowable(() ->
                    scoreBoard.updateScore(HOME_TEAM, "  ", 1, 0));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(TEAM_NAME_BLANK_MESSAGE);
        }
    }
}
