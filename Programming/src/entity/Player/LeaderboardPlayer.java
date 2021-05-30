package entity.Player;

public class LeaderboardPlayer {
    private int rank;
    private int elo;
    private int noPlayedMatch;
    private int noWonMatch;
    private float winningRate;
    private String username;


    public LeaderboardPlayer(int rank, String username, int noPlayedMatch, int noWonMatch, int elo) {
        this.username = username;
        this.rank = rank;
        this.elo = elo;
        this.noPlayedMatch = noPlayedMatch;
        this.noWonMatch = noWonMatch;
        this.winningRate = Math.round(((float)noWonMatch / noPlayedMatch)*100);
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

    public String getUsername() {return username;}

}
