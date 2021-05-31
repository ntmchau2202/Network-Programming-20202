package entity.Match;

import java.util.ArrayList;
import java.util.Random;

import entity.Player.Player;
import javafx.util.Pair;

public class Match {
	private Player player1;
	private Player player2;
	private int matchID;
	private ArrayList<Pair<Integer, Integer>> moveRecord;
	
	public Match(Player player1, Player player2) {
		// TODO: think about duplicated match ID here
		Random random = new Random();
		this.player1 = player1;
		this.player2 = player2;
		this.matchID = random.nextInt(9216);
		moveRecord = new ArrayList<Pair<Integer, Integer>>(); 
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
	
	public Pair<Integer, Integer> getLatestMove(){
		return this.moveRecord.get(moveRecord.size()-1);
	}
	
	public void addNewMoveRecord(Pair<Integer, Integer> newMove) {
		this.moveRecord.add(newMove);
	}
}
