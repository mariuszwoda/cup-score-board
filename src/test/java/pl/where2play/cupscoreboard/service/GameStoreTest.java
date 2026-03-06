package pl.where2play.cupscoreboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.where2play.cupscoreboard.exception.GameAlreadyExistsException;
import pl.where2play.cupscoreboard.exception.GameNotFoundException;
import pl.where2play.cupscoreboard.model.Game;
import pl.where2play.cupscoreboard.model.Score;
import pl.where2play.cupscoreboard.model.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests verifying that {@link InMemoryScoreBoard} delegates
 * all storage operations to the {@link GameStore} abstraction.
 *
 * <p>Uses Mockito to isolate the board from storage concerns,
 * and AssertJ for fluent, readable assertions.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InMemoryScoreBoard GameStore delegation")
class GameStoreTest {

    @Mock
    private GameStore gameStore;

    @InjectMocks
    private InMemoryScoreBoard scoreBoard;

    // -------------------------------------------------------------------------
    // Shared fixtures
    // -------------------------------------------------------------------------
    private static final String HOME = "Mexico";
    private static final String AWAY = "Canada";
    private static final Game EXISTING_GAME = new Game(new Team(HOME), new Team(AWAY));

    // =========================================================================
    @Nested
    @DisplayName("startGame()")
    class StartGame {

        @Test
        @DisplayName("should check store for duplicate before saving")
        void shouldCheckStoreForDuplicateBeforeSaving() {
            // Arrange
            when(gameStore.findByTeams(HOME, AWAY)).thenReturn(Optional.empty());

            // Act
            scoreBoard.startGame(HOME, AWAY);

            // Assert
            verify(gameStore).findByTeams(HOME, AWAY);
        }

        @Test
        @DisplayName("should persist a new game with 0-0 score when teams are not yet playing")
        void shouldPersistNewGameWithZeroScoreWhenNotAlreadyPlaying() {
            // Arrange
            when(gameStore.findByTeams(HOME, AWAY)).thenReturn(Optional.empty());

            // Act
            scoreBoard.startGame(HOME, AWAY);

            // Assert
            verify(gameStore).save(argThat(game ->
                    game.homeTeam().name().equals(HOME) &&
                            game.awayTeam().name().equals(AWAY) &&
                            game.score().home() + game.score().away() == 0
            ));
        }

        @Test
        @DisplayName("should throw GameAlreadyExistsException and never save when game is already live")
        void shouldThrowAndNeverSaveWhenGameAlreadyLive() {
            // Arrange
            when(gameStore.findByTeams(HOME, AWAY)).thenReturn(Optional.of(EXISTING_GAME));

            // Act & Assert
            assertThatThrownBy(() -> scoreBoard.startGame(HOME, AWAY))
                    .isInstanceOf(GameAlreadyExistsException.class);

            verify(gameStore, never()).save(any());
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("finishGame()")
    class FinishGame {

        @Test
        @DisplayName("should delegate removal to store when game exists")
        void shouldDelegateRemovalToStoreWhenGameExists() {
            // Arrange
            when(gameStore.findByTeams(HOME, AWAY)).thenReturn(Optional.of(EXISTING_GAME));

            // Act
            scoreBoard.finishGame(HOME, AWAY);

            // Assert
            verify(gameStore).remove(HOME, AWAY);
        }

        @Test
        @DisplayName("should throw GameNotFoundException and never remove when game not found")
        void shouldThrowAndNeverRemoveWhenGameNotFound() {
            // Arrange
            when(gameStore.findByTeams(HOME, AWAY)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> scoreBoard.finishGame(HOME, AWAY))
                    .isInstanceOf(GameNotFoundException.class)
                    .hasMessageContaining(HOME)
                    .hasMessageContaining(AWAY);

            verify(gameStore, never()).remove(any(), any());
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("updateScore()")
    class UpdateScore {

        @Test
        @DisplayName("should persist game with updated score when game exists")
        void shouldPersistGameWithUpdatedScoreWhenGameExists() {
            // Arrange
            when(gameStore.findByTeams(HOME, AWAY)).thenReturn(Optional.of(EXISTING_GAME));

            // Act
            scoreBoard.updateScore(HOME, AWAY, 2, 1);

            // Assert
            verify(gameStore).save(argThat(game ->
                    game.homeTeam().name().equals(HOME) &&
                            game.score().home() == 2 &&
                            game.score().away() == 1
            ));
        }

        @Test
        @DisplayName("should throw GameNotFoundException and never save when game not found")
        void shouldThrowAndNeverSaveWhenGameNotFound() {
            // Arrange
            when(gameStore.findByTeams(HOME, AWAY)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> scoreBoard.updateScore(HOME, AWAY, 2, 1))
                    .isInstanceOf(GameNotFoundException.class);

            verify(gameStore, never()).save(any());
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("getSummary()")
    class GetSummary {

        @Test
        @DisplayName("should retrieve all games from store")
        void shouldRetrieveAllGamesFromStore() {
            // Arrange
            when(gameStore.getAll()).thenReturn(List.of());

            // Act
            scoreBoard.getSummary();

            // Assert
            verify(gameStore).getAll();
        }

        @Test
        @DisplayName("should return games ordered by total score desc, then by recency desc")
        void shouldReturnGamesOrderedByTotalScoreThenRecency() {
            // Arrange — insertion order = oldest first (index 0 = oldest)
            Game mexicoCanada = new Game(new Team("Mexico"), new Team("Canada")).withScore(new Score(0, 5));
            Game spainBrazil = new Game(new Team("Spain"), new Team("Brazil")).withScore(new Score(10, 2));
            Game germanyFrance = new Game(new Team("Germany"), new Team("France")).withScore(new Score(2, 2));
            Game uruguayItaly = new Game(new Team("Uruguay"), new Team("Italy")).withScore(new Score(6, 6));
            Game argAustralia = new Game(new Team("Argentina"), new Team("Australia")).withScore(new Score(3, 1));

            when(gameStore.getAll()).thenReturn(
                    List.of(mexicoCanada, spainBrazil, germanyFrance, uruguayItaly, argAustralia));

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert — fluent single assertion over the whole sequence
            assertThat(summary)
                    .extracting(game -> game.homeTeam().name())
                    .as("Expected: Uruguay(12), Spain(12,older), Mexico(5), Argentina(4,newer), Germany(4,older)")
                    .containsExactly("Uruguay", "Spain", "Mexico", "Argentina", "Germany");
        }

        @Test
        @DisplayName("should return empty list when no games are live")
        void shouldReturnEmptyListWhenNoGamesAreLive() {
            // Arrange
            when(gameStore.getAll()).thenReturn(List.of());

            // Act
            List<Game> summary = scoreBoard.getSummary();

            // Assert
            assertThat(summary).isEmpty();
        }
    }
}
