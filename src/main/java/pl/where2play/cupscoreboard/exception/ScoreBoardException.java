package pl.where2play.cupscoreboard.exception;

/**
 * Base exception for all scoreboard-related failures.
 *
 * <p>All domain exceptions thrown by the scoreboard library extend this class,
 * allowing callers to catch the entire exception hierarchy with a single handler
 * when fine-grained handling is not needed.</p>
 */
public abstract class ScoreBoardException extends RuntimeException {

    protected ScoreBoardException(String message) {
        super(message);
    }
}

