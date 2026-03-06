package pl.where2play.cupscoreboard.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Game")
class GameTest {

    private static final Team HOME = new Team("Germany");
    private static final Team AWAY = new Team("France");

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("should initialize with zero score and correct teams")
        void shouldCreateGameWithInitialZeroScore() {
            // Arrange & Act
            Game game = new Game(HOME, AWAY);

            // Assert
            assertThat(game.homeTeam()).isEqualTo(HOME);
            assertThat(game.awayTeam()).isEqualTo(AWAY);
            assertThat(game.score()).isEqualTo(Score.ZERO);
        }

        @Test
        @DisplayName("should record start time between before and after construction")
        void shouldRecordStartTimeOnConstruction() {
            // Arrange
            Instant before = Instant.now();

            // Act
            Game game = new Game(HOME, AWAY);
            Instant after = Instant.now();

            // Assert
            assertThat(game.startedAt()).isBetween(before, after);
        }
    }

    @Nested
    @DisplayName("score update")
    class ScoreUpdate {

        private Game game;

        @BeforeEach
        void setUp() {
            game = new Game(HOME, AWAY);
        }

        @Test
        @DisplayName("should return new game instance with updated score")
        void shouldReturnNewGameWithUpdatedScore() {
            // Arrange
            Score newScore = new Score(2, 1);

            // Act
            Game updated = game.withScore(newScore);

            // Assert
            assertThat(updated.score()).isEqualTo(newScore);
        }

        @Test
        @DisplayName("should preserve teams and start time when score is updated")
        void shouldPreserveTeamsAndStartTimeWhenScoreUpdated() {
            // Arrange
            Score newScore = new Score(2, 1);

            // Act
            Game updated = game.withScore(newScore);

            // Assert
            assertThat(updated.homeTeam()).isEqualTo(HOME);
            assertThat(updated.awayTeam()).isEqualTo(AWAY);
            assertThat(updated.startedAt()).isEqualTo(game.startedAt());
        }

        @Test
        @DisplayName("should not mutate original game when score is updated")
        void shouldNotMutateOriginalGameWhenScoreUpdated() {
            // Arrange
            Score newScore = new Score(3, 0);

            // Act
            game.withScore(newScore);

            // Assert
            assertThat(game.score()).isEqualTo(Score.ZERO);
        }

        @Test
        @DisplayName("should throw NullPointerException when new score is null")
        void shouldThrowWhenNewScoreIsNull() {
            // Arrange & Act & Assert
            assertThatThrownBy(() -> game.withScore(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should throw NullPointerException when home team is null")
        void shouldThrowWhenHomeTeamIsNull() {
            // Arrange & Act & Assert
            assertThatThrownBy(() -> new Game(null, AWAY))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Home team must not be null");
        }

        @Test
        @DisplayName("should throw NullPointerException when away team is null")
        void shouldThrowWhenAwayTeamIsNull() {
            // Arrange & Act & Assert
            assertThatThrownBy(() -> new Game(HOME, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Away team must not be null");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when home and away teams are the same")
        void shouldThrowWhenHomeAndAwayTeamAreTheSame() {
            // Arrange
            Team same = new Team("Mexico");

            // Act & Assert
            assertThatThrownBy(() -> new Game(same, same))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Home and away teams must be different");
        }
    }

    @Nested
    @DisplayName("string representation")
    class StringRepresentation {

        @Test
        @DisplayName("should format toString as 'HomeTeam homeScore - AwayTeam awayScore'")
        void shouldFormatToStringCorrectly() {
            // Arrange
            Game game = new Game(HOME, AWAY);
            Game updated = game.withScore(new Score(3, 1));

            // Act
            String result = updated.toString();

            // Assert
            assertThat(result).isEqualTo("Germany 3 - France 1");
        }

        @Test
        @DisplayName("should format toString with zero scores on creation")
        void shouldFormatToStringWithZeroScoresOnCreation() {
            // Arrange & Act
            String result = new Game(HOME, AWAY).toString();

            // Assert
            assertThat(result).isEqualTo("Germany 0 - France 0");
        }
    }
}
