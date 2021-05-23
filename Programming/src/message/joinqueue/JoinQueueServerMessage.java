package message.joinqueue;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class JoinQueueServerMessage extends ServerMessage {
	private String username, sessionID;
	
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

	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
}
