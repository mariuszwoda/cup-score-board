package pl.where2play.cupscoreboard.model;

/**
 * Immutable value object representing a match score.
 */
public record Score(int home, int away) {

    public static final Score ZERO = new Score(0, 0);

    public Score {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public int totalGoals() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
