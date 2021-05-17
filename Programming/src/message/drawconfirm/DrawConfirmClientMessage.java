package message.drawconfirm;

import message.ClientMessage;
import protocol.Command;

public class DrawConfirmClientMessage extends ClientMessage {
	private int matchID;
	private String movePlayer, sessionID;
	private boolean acceptance;
	
	public DrawConfirmClientMessage(int matchID, String player, String sessionID, boolean acceptance) {
		super();
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.sessionID = sessionID;
		this.acceptance = acceptance;
		
		this.setCommand(Command.DRAW_CONFIRM);
		this.requestBody.createDrawConfirmBody(matchID, sessionID, player, acceptance);
		this.finalizeMessageObject();
	}
	
	public DrawConfirmClientMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = this.requestBody.getBody().getInt("match_id");
		this.movePlayer = this.requestBody.getBody().getString("move_player");
		this.sessionID = this.requestBody.getBody().getString("session_id");
		this.acceptance = this.requestBody.getBody().getBoolean("acceptance");
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
