package message.quitgame;

import message.ClientMessage;
import protocol.Command;

public class QuitGameClientMessage extends ClientMessage {
	private String username, sessionID;
	private int matchID;
	
	public QuitGameClientMessage(int matchID, String username, String sessionID) {
		super();
		this.matchID = matchID;
		this.username = username;
		this.sessionID = sessionID;
		
		this.setCommand(Command.QUIT_GAME);
		this.requestBody.createQuitGameBody(matchID, username, sessionID);
		this.finalizeMessageObject();
	}
	
	public QuitGameClientMessage(String input) {
		super(input);
		this.username = this.requestBody.getBody().getString("username");
		this.sessionID = this.requestBody.getBody().getString("session_id");
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
}
