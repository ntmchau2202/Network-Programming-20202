package message.joinqueue;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class JoinQueueServerMessage extends ServerMessage {
	private String username, sessionID, gameMode;
	
	public JoinQueueServerMessage(String usr, String sesID, String mode, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		this.username = usr;
		this.sessionID = sesID;
		this.gameMode = mode;
		
		this.setCommand(Command.JOIN_QUEUE);
		this.responseBody.createJoinQueueBody(usr, sesID);
		this.finalizeMessageObject();
	}
	
	public JoinQueueServerMessage(String input) {
		super(input);
		
		this.username = this.responseBody.getBody().getString("username");
		this.sessionID = this.responseBody.getBody().getString("session_id");
		this.gameMode = this.responseBody.getBody().getString("mode");

	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
	
	public String getGameMode() {
		return this.gameMode;
	}
}
