package pl.where2play.cupscoreboard.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@DisplayName("ScoreBoardException hierarchy")
class ScoreBoardExceptionTest {

    @Test
    @DisplayName("GameNotFoundException should be a ScoreBoardException")
    void gameNotFoundExceptionShouldExtendScoreBoardException() {
        GameNotFoundException ex = new GameNotFoundException("Mexico", "Canada");

        assertThat(ex).isInstanceOf(ScoreBoardException.class);
        assertThat(ex.getMessage()).contains("Mexico").contains("Canada");
    }

    @Test
    @DisplayName("GameAlreadyExistsException should be a ScoreBoardException")
    void gameAlreadyExistsExceptionShouldExtendScoreBoardException() {
        GameAlreadyExistsException ex = new GameAlreadyExistsException("Spain", "Brazil");

        assertThat(ex).isInstanceOf(ScoreBoardException.class);
        assertThat(ex.getMessage()).contains("Spain").contains("Brazil");
    }

    @Test
    @DisplayName("both exceptions should be catchable as ScoreBoardException")
    void shouldBeCatchableAsScoreBoardException() {
        assertThat(new GameNotFoundException("A", "B")).isInstanceOf(RuntimeException.class);
        assertThat(new GameAlreadyExistsException("A", "B")).isInstanceOf(RuntimeException.class);
    }
}

