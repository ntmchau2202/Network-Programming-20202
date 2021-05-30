package message.joinqueue;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class JoinQueueServerMessage extends ServerMessage {
	private String username, sessionID, player1, opponent;
	private int matchID, opponentElo;
	
	public JoinQueueServerMessage(String usr, String sesID, String opponent, int opponentElo, int matchID, String player1, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		this.username = usr;
		this.sessionID = sesID;
		
		this.setCommand(Command.JOIN_QUEUE);
		this.responseBody.createJoinQueueBody(usr, sesID, opponent, opponentElo, matchID, player1);
		this.finalizeMessageObject();
	}
	
	public JoinQueueServerMessage(String input) {
		super(input);
		
		this.username = this.responseBody.getBody().getString("username");
		this.sessionID = this.responseBody.getBody().getString("session_id");
		this.player1 = this.responseBody.getBody().getString("player_1");
		this.matchID = this.responseBody.getBody().getInt("match_id");
		this.opponent = this.responseBody.getBody().getString("opponent");
		this.opponentElo = this.responseBody.getBody().getInt("opponent_elo");

	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
	
	public String getFirstPlayer() {
		return this.player1;
	}
	
	public int getMatchID() {
		return this.matchID;
	}
	
	public String getOpponent() {
		return this.opponent;
	}
	
	public int getOpponentELO() {
		return this.opponentElo;
	}
}
