package pl.where2play.cupscoreboard.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("Team")
class TeamTest {

    private static final String VALID_NAME = "Mexico";
    private static final String BLANK_NAME_ERROR = "Team name must not be blank";

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("should create team with valid name")
        void shouldCreateTeamWithValidName() {
            // Arrange & Act
            Team team = new Team(VALID_NAME);

            // Assert
            assertThat(team.name()).isEqualTo(VALID_NAME);
        }

        @Test
        @DisplayName("should throw NullPointerException when name is null")
        void shouldThrowWhenNameIsNull() {
            // Arrange & Act & Assert
            assertThatThrownBy(() -> new Team(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Team name must not be null");
        }

        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @ParameterizedTest(name = "should reject blank name: [{arguments}]")
        @DisplayName("should throw IllegalArgumentException when name is blank")
        void shouldThrowWhenNameIsBlank(String name) {
            // Arrange & Act & Assert
            assertThatThrownBy(() -> new Team(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(BLANK_NAME_ERROR);
        }

        @Test
        @DisplayName("should trim leading and trailing whitespace from name")
        void shouldTrimTeamName() {
            // Arrange
            String nameWithWhitespace = "  Brazil  ";

            // Act
            Team team = new Team(nameWithWhitespace);

            // Assert
            assertThat(team.name()).isEqualTo("Brazil");
        }
    }

    @Nested
    @DisplayName("equality and hashCode")
    class EqualityAndHashCode {

        @Test
        @DisplayName("should be equal when names are the same")
        void shouldBeEqualWhenSameName() {
            // Arrange
            Team teamA = new Team("Spain");
            Team teamB = new Team("Spain");

            // Assert
            assertThat(teamA).isEqualTo(teamB);
        }

        @Test
        @DisplayName("should not be equal when names differ")
        void shouldNotBeEqualWhenDifferentName() {
            // Arrange
            Team teamA = new Team("Spain");
            Team teamB = new Team("France");

            // Assert
            assertThat(teamA).isNotEqualTo(teamB);
        }

        @Test
        @DisplayName("should differentiate names by case")
        void shouldBeCaseSensitive() {
            // Arrange
            Team lowerCase = new Team("spain");
            Team upperCase = new Team("Spain");

            // Assert
            assertThat(lowerCase).isNotEqualTo(upperCase);
        }

        @Test
        @DisplayName("should have equal hashCodes for equal teams")
        void shouldHaveConsistentHashCode() {
            // Arrange
            Team teamA = new Team("Spain");
            Team teamB = new Team("Spain");

            // Assert
            assertThat(teamA.hashCode()).hasSameHashCodeAs(teamB.hashCode());
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringRepresentation {

        @Test
        @DisplayName("should contain team name in string representation")
        void shouldContainNameInToString() {
            // Arrange
            Team team = new Team(VALID_NAME);

            // Act
            String result = team.toString();

            // Assert
            assertThat(result).contains(VALID_NAME);
        }
    }
}
