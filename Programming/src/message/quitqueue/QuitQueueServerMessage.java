package message.quitqueue;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class QuitQueueServerMessage extends ServerMessage {
	
	private String username;

	public QuitQueueServerMessage(String usr, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		this.username = usr;
		
		this.setCommand(Command.QUIT_QUEUE);
		this.responseBody.createQuitQueueBody(usr);
		this.finalizeMessageObject();
	}
	
	public QuitQueueServerMessage(String input) {
		super(input);
		
		this.username = this.responseBody.getBody().getString("username");
	}
	
	public String getUsername() {
		return this.username;
	}

}
