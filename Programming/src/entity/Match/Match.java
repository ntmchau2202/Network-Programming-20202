package entity.Match;

import java.util.Random;

import entity.Player.Player;

public class Match {
	private Player player1;
	private Player player2;
	private int matchID;
	
	public Match(Player player1, Player player2) {
		// TODO: think about duplicated match ID here
		Random random = new Random();
		this.player1 = player1;
		this.player2 = player2;
		this.matchID = random.nextInt(9216);
	}
	
	public Player getPlayer1() {
		return this.player1;
	}
	
	public Player getPlayer2() {
		return this.player2;
	}
	
	public int getMatchID() {
		return this.matchID;
	}
	
	public boolean isPlayerOfMatch(Player player) {
		return player.getSessionId().equalsIgnoreCase(player1.getSessionId()) || player.getSessionId().equalsIgnoreCase(player2.getSessionId());
	}
	
	
}
