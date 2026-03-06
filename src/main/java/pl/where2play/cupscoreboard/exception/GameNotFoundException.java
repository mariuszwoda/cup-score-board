package pl.where2play.cupscoreboard.exception;

/**
 * Thrown when a game operation targets a game that does not exist on the scoreboard.
 */
public class GameNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Game not found: %s vs %s";

    public GameNotFoundException(String homeTeam, String awayTeam) {
        super(MESSAGE_TEMPLATE.formatted(homeTeam, awayTeam));
    }
}
