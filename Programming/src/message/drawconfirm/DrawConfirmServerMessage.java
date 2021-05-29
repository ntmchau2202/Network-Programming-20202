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
		this.responseBody.createDrawConfirmBody(matchID, player, acceptance);
		this.finalizeMessageObject();
	}
	
	public DrawConfirmServerMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = this.responseBody.getBody().getInt("match_id");
		this.movePlayer = this.responseBody.getBody().getString("move_player");
		this.sessionID = this.responseBody.getBody().getString("session_id");
		this.acceptance = this.responseBody.getBody().getBoolean("acceptance");
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