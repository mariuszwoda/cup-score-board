package pl.where2play.cupscoreboard.exception;

/**
 * Thrown when attempting to start a game that is already in progress on the scoreboard.
 */
public class GameAlreadyExistsException extends ScoreBoardException {

    private static final String MESSAGE_TEMPLATE = "Game already in progress: %s vs %s";

    public GameAlreadyExistsException(String homeTeam, String awayTeam) {
        super(MESSAGE_TEMPLATE.formatted(homeTeam, awayTeam));
    }
}
