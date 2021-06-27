package message.drawconfirm;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class DrawConfirmServerMessage extends ServerMessage {
	private int matchID;
	private String movePlayer, sessionID;
	private boolean acceptance;
	// should we add a field of draw msg id here?

	// player: confirm player
	// sessionID is the sessionID of listen message
	public DrawConfirmServerMessage(int messageCommandID, int matchID, String player, String sessionID,
									boolean acceptance, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, messageCommandID);
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.sessionID = sessionID;
		this.acceptance = acceptance;
		
		this.setCommand(Command.DRAW_CONFIRM);
		this.responseBody.createDrawConfirmBody(matchID, player, sessionID, acceptance);
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
