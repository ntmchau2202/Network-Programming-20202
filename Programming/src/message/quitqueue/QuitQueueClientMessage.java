package message.quitqueue;

import message.ClientMessage;
import protocol.Command;

public class QuitQueueClientMessage extends ClientMessage {
	private String username, sessionID;
	
	public QuitQueueClientMessage(String username, String sesID) {
		super();

		this.username = username;
		this.sessionID = sesID;
		this.setCommand(Command.QUIT_QUEUE);
		this.requestBody.createQuitQueueBody(username, sesID);
		this.finalizeMessageObject();
	}
	
	public QuitQueueClientMessage(String inputMessage) {
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
