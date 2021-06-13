package message.chat;

import message.ClientMessage;
import protocol.Command;

public class ListenChatClientMessage extends ClientMessage {
	private String username;
	private int matchID;
	
	public ListenChatClientMessage(String username, int matchID) {
		super();
		this.username = username;
		this.matchID = matchID;
		
		this.setCommand(Command.LISTEN_MOVE);
		this.requestBody.createListenMoveBody(username, matchID);
		this.finalizeMessageObject();
		
	}
	
	public ListenChatClientMessage(String inputMessage) {
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
