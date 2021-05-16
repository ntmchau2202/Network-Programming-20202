package message.drawrequest;

import message.ClientMessage;
import protocol.Command;

public class DrawRequestClientMessage extends ClientMessage {
	private int matchID;
	private String movePlayer, sessionID;
	
	public DrawRequestClientMessage(int matchID, String player, String sessionID) {
		super();
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.sessionID = sessionID;
		
		this.setCommand(Command.DRAW_REQUEST);
		this.requestBody.createDrawRequestBody(matchID, sessionID, player);
		this.finalizeMessageObject();
	}
	
	public DrawRequestClientMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = this.requestBody.getBody().getInt("match_id");
		this.movePlayer = this.requestBody.getBody().getString("move_player");
		this.sessionID = this.requestBody.getBody().getString("session_id");
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
