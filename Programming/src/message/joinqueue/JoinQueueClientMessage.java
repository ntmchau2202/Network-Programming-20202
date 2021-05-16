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
		this.requestBody.createJoinQueueBody(mode, sesID);
		this.finalizeMessageObject();
	}
	
	public JoinQueueClientMessage(String inputMessage) {
		super(inputMessage);
		this.gameMode = this.requestBody.getBody().getString("mode");
		this.sessionID = this.requestBody.getBody().getString("session_id");
	}	
	
	public String getGameMode() {
		return this.gameMode;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
}
