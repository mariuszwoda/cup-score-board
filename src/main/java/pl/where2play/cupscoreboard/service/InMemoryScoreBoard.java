package pl.where2play.cupscoreboard.service;

import pl.where2play.cupscoreboard.model.Game;

import java.util.List;

/**
 * In-memory implementation of {@link ScoreBoard}.
 */
public class InMemoryScoreBoard implements ScoreBoard {

    @Override
    public Game startGame(String homeTeam, String awayTeam) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void finishGame(String homeTeam, String awayTeam) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Game updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<Game> getSummary() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
