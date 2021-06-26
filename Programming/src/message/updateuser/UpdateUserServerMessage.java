package message.updateuser;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class UpdateUserServerMessage extends ServerMessage {
	
	private int rank, elo, noMatchPlayed, noMatchWon;
	private float winRate;
	private String username;
	
	public UpdateUserServerMessage(int messageCommandID, String username, int elo, int rank, float wRate, int nMatchPlayed, int nMatchWon, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, messageCommandID);
		this.username = username;
		this.elo = elo;
		this.rank = rank;
		this.noMatchPlayed = nMatchPlayed;
		this.noMatchWon = nMatchWon;
		this.winRate = wRate;
		
		this.setCommand(Command.UPDATE_USER);
		this.responseBody.createUpdateUserBody(username, nMatchPlayed, nMatchWon, elo, rank, Float.toString(winRate));
		this.finalizeMessageObject();
	}
	
	public UpdateUserServerMessage(String input) {
		super(input);
		this.username = this.responseBody.getBody().getString("username");
		this.elo = this.responseBody.getBody().getInt("elo");
		this.rank = this.responseBody.getBody().getInt("rank");
		this.winRate = this.responseBody.getBody().getFloat("win_rate");
		this.noMatchPlayed = this.responseBody.getBody().getInt("num_game_played");
		this.noMatchWon = this.responseBody.getBody().getInt("num_game_won");
	}

	public int getRank() {
		return rank;
	}

	public int getElo() {
		return elo;
	}

	public int getNoMatchPlayed() {
		return noMatchPlayed;
	}

	public int getNoMatchWon() {
		return noMatchWon;
	}

	public float getWinRate() {
		return winRate;
	}

	public String getUsername() {
		return username;
	}

	
}
