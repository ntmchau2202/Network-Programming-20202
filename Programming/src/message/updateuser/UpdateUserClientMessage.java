package message.updateuser;

import message.ClientMessage;
import protocol.Command;

public class UpdateUserClientMessage extends ClientMessage {
	private String username, sessionID;
	
	public UpdateUserClientMessage(String username, String sessionID) {
		super();
		
		this.username = username;
		this.sessionID = sessionID;

		this.setCommand(Command.UPDATE_USER);
		this.requestBody.createUpdateUserBody(username, sessionID);
		this.finalizeMessageObject();
	}
	
	public UpdateUserClientMessage(String inputMessage) {
		super(inputMessage);

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
