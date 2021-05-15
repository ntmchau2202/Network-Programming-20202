package entity.Player;

public class RankPlayer extends Player {
    private int rank;
    private int elo;
    private int noPlayedMatch;
    private int noWonMatch;
    private float winningRate;

    public RankPlayer(String username, String sessionId, int elo) {
        super(username, sessionId);
        this.elo = elo;
    }

    public RankPlayer(String username, String sessionId, int rank, int elo, int noPlayedMatch, int noWonMatch) {
        super(username, sessionId);
        this.rank = rank;
        this.elo = elo;
        this.noPlayedMatch = noPlayedMatch;
        this.noWonMatch = noWonMatch;
        this.winningRate = (float)noWonMatch / noPlayedMatch;
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

    public void updatePlayerInfo(int rank, int elo, int noPlayedMatch, int noWonMatch) {
        this.rank = rank;
        this.elo = elo;
        this.noPlayedMatch = noPlayedMatch;
        this.noWonMatch = noWonMatch;
        this.winningRate = (float)noWonMatch / noPlayedMatch;
    }
}
