package message.drawrequest;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class DrawRequestServerMessage extends ServerMessage {

	private int matchID;
	private String movePlayer, sessionID;
	
	public DrawRequestServerMessage(int matchID, String player, String sessionID, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.sessionID = sessionID;
		
		this.setCommand(Command.DRAW_REQUEST);
		this.responseBody.createDrawRequestBody();
		this.finalizeMessageObject();

	}
	
	public DrawRequestServerMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = (int)this.responseBody.getKey("match_id");
		this.movePlayer = (String)this.responseBody.getKey("move_player");
		this.sessionID = (String)this.responseBody.getKey("session_id");

	}

	public int getMatchID() {
		return this.matchID;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
	
	public String getPlayer() {
		return this.movePlayer;
	}
	
}
