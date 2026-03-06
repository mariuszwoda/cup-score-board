package pl.where2play.cupscoreboard.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Score")
class ScoreTest {

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("should create score with valid home and away values")
        void shouldCreateScoreWithValidValues() {
            // Arrange & Act
            Score score = new Score(3, 1);

            // Assert
            assertThat(score.home()).isEqualTo(3);
            assertThat(score.away()).isEqualTo(1);
        }

        @Test
        @DisplayName("should create zero-zero score using ZERO constant")
        void shouldCreateZeroZeroScoreUsingConstant() {
            // Arrange & Act
            Score score = Score.ZERO;

            // Assert
            assertThat(score.home()).isZero();
            assertThat(score.away()).isZero();
        }

        @Test
        @DisplayName("should allow zero values for both home and away")
        void shouldAllowZeroValues() {
            // Arrange & Act
            Score score = new Score(0, 0);

            // Assert
            assertThat(score.home()).isZero();
            assertThat(score.away()).isZero();
        }
    }

    @Nested
    @DisplayName("total goals calculation")
    class TotalGoalsCalculation {

        @CsvSource({"0, 5, 5", "10, 2, 12", "6, 6, 12", "3, 1, 4", "0, 0, 0"})
        @ParameterizedTest(name = "home={0}, away={1} => total={2}")
        @DisplayName("should calculate total goals correctly")
        void shouldCalculateTotalGoals(int home, int away, int expectedTotal) {
            // Arrange
            Score score = new Score(home, away);

            // Act
            int total = score.totalGoals();

            // Assert
            assertThat(total).isEqualTo(expectedTotal);
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @CsvSource({"-1, 0", "0, -1", "-1, -1", "-5, -10"})
        @ParameterizedTest(name = "home={0}, away={1} should be rejected")
        @DisplayName("should throw IllegalArgumentException when any score is negative")
        void shouldThrowWhenScoreIsNegative(int home, int away) {
            // Arrange & Act & Assert
            assertThatThrownBy(() -> new Score(home, away))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Score values must not be negative");
        }
    }

    @Nested
    @DisplayName("equality and hashcode")
    class EqualityAndHashCode {

        @Test
        @DisplayName("should be equal when home and away values are the same")
        void shouldBeEqualWhenSameValues() {
            // Arrange
            Score score1 = new Score(2, 3);
            Score score2 = new Score(2, 3);

            // Act & Assert
            assertThat(score1).isEqualTo(score2);
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void shouldNotBeEqualWhenValuesDiffer() {
            // Arrange
            Score score1 = new Score(2, 3);
            Score score2 = new Score(3, 2);

            // Act & Assert
            assertThat(score1).isNotEqualTo(score2);
        }

        @Test
        @DisplayName("ZERO constant should equal new Score(0, 0)")
        void shouldZeroConstantEqualNewZeroScore() {
            // Arrange & Act & Assert
            assertThat(new Score(0, 0)).isEqualTo(Score.ZERO);
        }

        @Test
        @DisplayName("should have consistent hashCode for equal scores")
        void shouldHaveConsistentHashCodeForEqualScores() {
            // Arrange
            Score score1 = new Score(2, 3);
            Score score2 = new Score(2, 3);

            // Act & Assert
            assertThat(score1.hashCode()).hasSameHashCodeAs(score2.hashCode());
        }
    }
}
