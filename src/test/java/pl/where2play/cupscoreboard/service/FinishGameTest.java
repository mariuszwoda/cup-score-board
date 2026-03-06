package pl.where2play.cupscoreboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.where2play.cupscoreboard.exception.GameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("ScoreBoard.finishGame()")
class FinishGameTest {

    private static final String HOME_TEAM = "Uruguay";
    private static final String AWAY_TEAM = "Italy";
    private static final String OTHER_HOME = "Germany";
    private static final String OTHER_AWAY = "France";
    private static final String TEAM_NAME_NULL_MESSAGE = "Team name must not be null";
    private static final String TEAM_NAME_BLANK_MESSAGE = "Team name must not be blank";

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
    @DisplayName("when finishing an existing game")
    class WhenFinishingExistingGame {

        @Test
        @DisplayName("should remove the finished game from the scoreboard")
        void shouldRemoveGameFromScoreboard() {
            // Act
            scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM);

            // Assert
            assertThat(scoreBoard.getSummary()).isEmpty();
        }

        @Test
        @DisplayName("should only remove the finished game, leaving others intact")
        void shouldOnlyRemoveTheFinishedGame() {
            // Arrange
            scoreBoard.startGame(OTHER_HOME, OTHER_AWAY);

            // Act
            scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM);

            // Assert
            assertThat(scoreBoard.getSummary()).hasSize(1).first()
                    .satisfies(game -> assertSoftly(softly -> {
                        softly.assertThat(game.homeTeam().name()).isEqualTo(OTHER_HOME);
                        softly.assertThat(game.awayTeam().name()).isEqualTo(OTHER_AWAY);
                    }));
        }

        @Test
        @DisplayName("should be case-insensitive for team names")
        void shouldBeCaseInsensitiveForTeamNames() {
            // Act
            scoreBoard.finishGame(HOME_TEAM.toLowerCase(), AWAY_TEAM.toUpperCase());

            // Assert
            assertThat(scoreBoard.getSummary()).isEmpty();
        }

        @Test
        @DisplayName("should allow the same teams to start a new game after finishing")
        void shouldAllowSameTeamToPlayAfterFinish() {
            // Arrange
            scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM);

            // Act
            scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);

            // Assert
            assertThat(scoreBoard.getSummary()).hasSize(1);
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
            Throwable thrown = catchThrowable(() -> scoreBoard.finishGame(OTHER_HOME, OTHER_AWAY));

            // Assert
            assertThat(thrown).isInstanceOf(GameNotFoundException.class);
        }

        @Test
        @DisplayName("should throw GameNotFoundException when finishing an already finished game")
        void shouldThrowWhenGameAlreadyFinished() {
            // Arrange
            scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM);

            // Act
            Throwable thrown = catchThrowable(() -> scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM));

            // Assert
            assertThat(thrown).isInstanceOf(GameNotFoundException.class);
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
            Throwable thrown = catchThrowable(() -> scoreBoard.finishGame(null, AWAY_TEAM));

            // Assert
            assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(TEAM_NAME_NULL_MESSAGE);
        }

        @Test
        @DisplayName("should throw NullPointerException when away team is null")
        void shouldThrowWhenAwayTeamIsNull() {
            // Act
            Throwable thrown = catchThrowable(() -> scoreBoard.finishGame(HOME_TEAM, null));

            // Assert
            assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(TEAM_NAME_NULL_MESSAGE);
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
            Throwable thrown = catchThrowable(() -> scoreBoard.finishGame("  ", AWAY_TEAM));

            // Assert
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(TEAM_NAME_BLANK_MESSAGE);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when away team is blank")
        void shouldThrowWhenAwayTeamIsBlank() {
            // Act
            Throwable thrown = catchThrowable(() -> scoreBoard.finishGame(HOME_TEAM, "  "));

            // Assert
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(TEAM_NAME_BLANK_MESSAGE);
        }
    }
}
