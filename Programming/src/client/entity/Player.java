package client.entity;

public class Player {
    private final String username;
    private final String sessionId;
    private int rank;
    private int elo;
    private int noPlayedMatch;
    private int noWonMatch;
    private float winningRate;

    public Player(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
    }

    public Player(String username, String sessionId, int elo) {
        this.username = username;
        this.sessionId = sessionId;
        this.elo = elo;
    }

    public Player(String username, String sessionId, int rank, int elo, int noPlayedMatch, int noWonMatch, float winningRate) {
        this.username = username;
        this.sessionId = sessionId;
        this.rank = rank;
        this.elo = elo;
        this.noPlayedMatch = noPlayedMatch;
        this.noWonMatch = noWonMatch;
        this.winningRate = winningRate;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getRank() {
        return rank;
    }

    public int getElo() {
        return elo;
    }

    public int getNoPlayedMatch() {
        return noPlayedMatch;
    }

    public int getNoWonMatch() {
        return noWonMatch;
    }

    public float getWinningRate() {
        return winningRate;
    }
}
