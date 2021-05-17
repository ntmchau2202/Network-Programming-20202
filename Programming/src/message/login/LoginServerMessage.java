package message.login;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;
import java.math.*;

public class LoginServerMessage extends ServerMessage {
	private int rank, elo, noMatchPlayed, noMatchWon;
	private float winRate;
	private String username, sessionID;
	
	public LoginServerMessage(String username, String sessionID, int elo, int rank, float wRate, int nMatchPlayed, int nMatchWon, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		this.username = username;
		this.sessionID = sessionID;
		this.elo = elo;
		this.rank = rank;
		this.noMatchPlayed = nMatchPlayed;
		this.noMatchWon = nMatchWon;
		this.winRate = wRate;
		
		this.setCommand(Command.LOGIN);
		this.responseBody.createLoginBody(username, sessionID, elo, rank, wRate, nMatchPlayed, nMatchWon);
		this.finalizeMessageObject();
	}
	
	public LoginServerMessage(String input) {
		super(input);
		this.username = this.responseBody.getBody().getString("username");
		this.sessionID = this.responseBody.getBody().getString("session_id");
		this.elo = this.responseBody.getBody().getInt("elo");
		this.rank = this.responseBody.getBody().getInt("rank");
		this.winRate = this.responseBody.getBody().getFloat("win_rate");
		this.noMatchPlayed = this.responseBody.getBody().getInt("num_game_played");
		this.noMatchWon = this.responseBody.getBody().getInt("num_game_won");
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
	
	public int getELO() {
		return this.elo;
	}
	
	public int getRank() {
		return this.rank;
	}
	
	public float getWinrate() {
		return this.winRate;
	}
	
	public int getNumberOfMatchPlayed() {
		return this.noMatchPlayed;
	}
	
	public int getNumberOfMatchWon() {
		return this.noMatchWon;
	}
}
