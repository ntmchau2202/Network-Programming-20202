package entity.Match;

import java.util.ArrayList;
import java.util.Random;

import entity.Move.Move;
import entity.Player.Player;
import javafx.util.Pair;

public class Match {
	private Player player1;
	private Player player2;
	private int matchID;
	private ArrayList<Move> moveRecord;
	
	public Match(Player player1, Player player2) {
		// TODO: think about duplicated match ID here
		Random random = new Random();
		this.player1 = player1;
		this.player2 = player2;
		this.matchID = random.nextInt(9216);
		moveRecord = new ArrayList<Move>(); 
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
	
	public Move getLatestMove(){
		return this.moveRecord.get(moveRecord.size()-1);
	}
	
	public void addNewMoveRecord(int x, int y, String movePlayer, String state, String result) {
		Move newMove = new Move(x, y, movePlayer, state, result);
		this.moveRecord.add(newMove);
	}
	
	public int getNumberOfMoves() {
		return this.moveRecord.size();
	}
}
