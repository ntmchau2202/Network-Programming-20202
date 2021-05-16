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
		this.requestBody.createDrawRequestBody();
		this.finalizeMessageObject();
	}
	
	public DrawConfirmClientMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = (int)this.requestBody.getKey("match_id");
		this.movePlayer = (String)this.requestBody.getKey("move_player");
		this.sessionID = (String)this.requestBody.getKey("session_id");
		this.acceptance = (boolean)this.requestBody.getKey("acceptance");
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
