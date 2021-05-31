package message.move;

import message.ClientMessage;
import protocol.Command;

public class ListenMoveClientMessage extends ClientMessage {
	
	private String username;
	private int matchID;
	
	public ListenMoveClientMessage(String username, int matchID) {
		super();
		this.username = username;
		this.matchID = matchID;
		
		this.setCommand(Command.LISTEN_MOVE);
		this.requestBody.createListenMoveBody(username, matchID);
		this.finalizeMessageObject();
		
	}
	
	public ListenMoveClientMessage(String inputMessage) {
		super(inputMessage);
		
		this.username = this.requestBody.getBody().getString("username");
		this.matchID = this.requestBody.getBody().getInt("match_id");
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public int getMatchID() {
		return this.matchID;
	}
}
