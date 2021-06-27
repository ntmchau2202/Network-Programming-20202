package message.drawrequest;

import message.ClientMessage;
import protocol.Command;

public class DrawRequestClientMessage extends ClientMessage {
	private int matchID;
	private String requestPlayerName, requestSessionID;
	
	public DrawRequestClientMessage(int matchID, String requestPlayerName, String requestSessionID) {
		super();
		
		this.matchID = matchID;
		this.requestPlayerName = requestPlayerName;
		this.requestSessionID = requestSessionID;
		
		this.setCommand(Command.DRAW_REQUEST);
		this.requestBody.createDrawRequestBody(matchID, requestSessionID, requestPlayerName);
		this.finalizeMessageObject();
	}
	
	public DrawRequestClientMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = this.requestBody.getBody().getInt("match_id");
		this.requestSessionID = this.requestBody.getBody().getString("session_id");
		this.requestPlayerName = this.requestBody.getBody().getString("move_player");
	}
	
	public int getMatchID() {
		return this.matchID;
	}

	public String getRequestPlayerName() {
		return requestPlayerName;
	}

	public String getRequestSessionID() {
		return requestSessionID;
	}
}
