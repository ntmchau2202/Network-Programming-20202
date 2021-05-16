package message.drawconfirm;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class DrawConfirmServerMessage extends ServerMessage {
	private int matchID;
	private String movePlayer, sessionID;
	private boolean acceptance;
	
	public DrawConfirmServerMessage(int matchID, String player, String sessionID, boolean acceptance, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.sessionID = sessionID;
		this.acceptance = acceptance;
		
		this.setCommand(Command.DRAW_CONFIRM);
		this.responseBody.createDrawRequestBody();
		this.finalizeMessageObject();
	}
	
	public DrawConfirmServerMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = (int)this.responseBody.getKey("match_id");
		this.movePlayer = (String)this.responseBody.getKey("move_player");
		this.sessionID = (String)this.responseBody.getKey("session_id");
		this.acceptance = (boolean)this.responseBody.getKey("acceptance");
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
	
	public boolean getAcceptance() {
		return this.acceptance;
	}
}
