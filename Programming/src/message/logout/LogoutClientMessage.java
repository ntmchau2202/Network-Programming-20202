package message.logout;

import message.ClientMessage;
import protocol.Command;

public class LogoutClientMessage extends ClientMessage {
	private String username, sessionID;
	
	public LogoutClientMessage(String username, String sessionID) {
		super();
		this.username = username;
		this.sessionID = sessionID;
		
		this.setCommand(Command.LOGOUT);
		this.requestBody.createLogoutBody(username, sessionID);
		this.finalizeMessageObject();
	}
	
	public LogoutClientMessage(String input) {
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
