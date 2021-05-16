package message.logout;

import message.ClientMessage;

public class LogoutClientMessage extends ClientMessage {
	private String username, sessionID;
	
	public LogoutClientMessage(String username, String sessionID) {
		super();
		this.username = username;
		this.sessionID = sessionID;
		
		this.requestBody.createLogoutBody();
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
