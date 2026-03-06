package pl.where2play.cupscoreboard.model;

/**
 * Immutable value object representing a match score.
 */
public record Score(int home, int away) {

    public static final Score ZERO = new Score(0, 0);

    public Score {
        if (home < 0 || away < 0) {
            throw new IllegalArgumentException(
                    "Score values must not be negative, but got: home=%d, away=%d".formatted(home, away)
            );
        }
    }

    public int totalGoals() {
        return home + away;
    }
}
