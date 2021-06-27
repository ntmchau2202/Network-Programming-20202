package message.drawrequest;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class DrawRequestServerMessage extends ServerMessage {

	private int matchID;
	private String movePlayer, sessionID;
	//	should we add a field of draw msg id here?
	
	public DrawRequestServerMessage(int messageCommandID, int matchID, String player,
									String sessionID, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, messageCommandID);
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.sessionID = sessionID;
		
		this.setCommand(Command.DRAW_REQUEST);
		this.responseBody.createDrawRequestBody(matchID, player);
		this.finalizeMessageObject();

	}
	
	public DrawRequestServerMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = this.responseBody.getBody().getInt("match_id");
		this.movePlayer = this.responseBody.getBody().getString("move_player");
		this.sessionID = this.responseBody.getBody().getString("session_id");

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
