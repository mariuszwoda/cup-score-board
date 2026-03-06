package pl.where2play.cupscoreboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.where2play.cupscoreboard.exception.GameAlreadyExistsException;
import pl.where2play.cupscoreboard.model.Game;
import pl.where2play.cupscoreboard.model.Score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("ScoreBoard.startGame()")
class StartGameTest {

    private static final String HOME_TEAM = "Mexico";
    private static final String AWAY_TEAM = "Canada";
    private static final String TEAM_NAME_NULL_MESSAGE = "Team name must not be null";
    private static final String TEAM_NAME_BLANK_MESSAGE = "Team name must not be blank";

    private ScoreBoard scoreBoard;

    @BeforeEach
    void setUp() {
        // Arrange (shared) — fresh board for every test
        scoreBoard = new InMemoryScoreBoard();
    }

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when starting a valid game")
    class WhenStartingValidGame {

        @Test
        @DisplayName("should return game with correct teams, zero score, and a start time")
        void shouldStartGameWithInitialState() {
            // Act
            Game game = scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);

            // Assert — all fields of the returned game in one soft block
            assertSoftly(softly -> {
                softly.assertThat(game.homeTeam().name()).isEqualTo(HOME_TEAM);
                softly.assertThat(game.awayTeam().name()).isEqualTo(AWAY_TEAM);
                softly.assertThat(game.score()).isEqualTo(Score.ZERO);
                softly.assertThat(game.startedAt()).isNotNull();
            });
        }

        @Test
        @DisplayName("should add the game to the summary")
        void shouldAppearInSummaryAfterStart() {
            // Act
            Game game = scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);

            // Assert
            assertThat(scoreBoard.getSummary())
                    .hasSize(1)
                    .containsExactly(game);
        }

        @Test
        @DisplayName("should preserve original casing of team names")
        void shouldPreserveTeamNameCasing() {
            // Arrange
            String mixedCaseHome = "mExIcO";
            String mixedCaseAway = "cAnAdA";

            // Act
            Game game = scoreBoard.startGame(mixedCaseHome, mixedCaseAway);

            // Assert
            assertSoftly(softly -> {
                softly.assertThat(game.homeTeam().name()).isEqualTo(mixedCaseHome);
                softly.assertThat(game.awayTeam().name()).isEqualTo(mixedCaseAway);
            });
        }
    }

    // -------------------------------------------------------------------------
    // Duplicate fixture
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when game already exists")
    class WhenGameAlreadyExists {

        @BeforeEach
        void arrangeExistingGame() {
            // Arrange — seed one live game before each test in this group
            scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);
        }

        @Test
        @DisplayName("should throw GameAlreadyExistsException for exact duplicate fixture")
        void shouldThrowWhenSameFixtureAlreadyLive() {
            // Act
            Throwable thrown = catchThrowable(() -> scoreBoard.startGame(HOME_TEAM, AWAY_TEAM));

            // Assert
            assertThat(thrown).isInstanceOf(GameAlreadyExistsException.class);
        }

        @Test
        @DisplayName("should throw GameAlreadyExistsException for duplicate fixture regardless of case")
        void shouldThrowWhenSameFixtureLiveWithDifferentCase() {
            // Act
            Throwable thrown = catchThrowable(() -> scoreBoard.startGame(
                    HOME_TEAM.toLowerCase(), AWAY_TEAM.toLowerCase()));

            // Assert
            assertThat(thrown).isInstanceOf(GameAlreadyExistsException.class);
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
            Throwable thrown = catchThrowable(() -> scoreBoard.startGame(null, AWAY_TEAM));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining(TEAM_NAME_NULL_MESSAGE);
        }

        @Test
        @DisplayName("should throw NullPointerException when away team is null")
        void shouldThrowWhenAwayTeamIsNull() {
            // Act
            Throwable thrown = catchThrowable(() -> scoreBoard.startGame(HOME_TEAM, null));

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
            Throwable thrown = catchThrowable(() -> scoreBoard.startGame("  ", AWAY_TEAM));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(TEAM_NAME_BLANK_MESSAGE);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when away team is blank")
        void shouldThrowWhenAwayTeamIsBlank() {
            // Act
            Throwable thrown = catchThrowable(() -> scoreBoard.startGame(HOME_TEAM, "  "));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(TEAM_NAME_BLANK_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // Same team
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("when home and away teams are the same")
    class WhenTeamsAreTheSame {

        @Test
        @DisplayName("should throw IllegalArgumentException when home and away teams are identical")
        void shouldThrowWhenHomeAndAwayTeamAreTheSame() {
            // Act
            Throwable thrown = catchThrowable(() -> scoreBoard.startGame(HOME_TEAM, HOME_TEAM));

            // Assert
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
