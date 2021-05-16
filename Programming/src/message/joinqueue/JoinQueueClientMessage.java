package message.joinqueue;

import message.ClientMessage;
import protocol.Command;

public class JoinQueueClientMessage extends ClientMessage {
	private String gameMode;
	private String sessionID;
	
	public JoinQueueClientMessage(String mode, String sesID) {
		super();
		
		this.gameMode = mode;
		this.sessionID = sesID;
		
		this.setCommand(Command.JOIN_QUEUE);
		this.requestBody.createJoinQueueBody();
		this.finalizeMessageObject();
	}
	
	public JoinQueueClientMessage(String inputMessage) {
		super(inputMessage);
		this.gameMode = (String)this.requestBody.getKey("mode");
		this.sessionID = (String)this.requestBody.getKey("session_id");
	}	
	
	public String getGameMode() {
		return this.gameMode;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
}
