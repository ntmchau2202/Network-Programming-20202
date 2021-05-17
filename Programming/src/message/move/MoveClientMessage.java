package message.move;

import org.json.JSONObject;

import message.ClientMessage;
import protocol.Command;

public class MoveClientMessage extends ClientMessage {
	private int matchID, posX, posY;
	private String sessionID, movePlayer, state, result;
	
	public MoveClientMessage(int matchID, String player, String sessionID, int posX, int posY, String state, String res) {
		super();
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.sessionID = sessionID;
		this.posX = posX;
		this.posY = posY;
		this.state = state;
		this.result = res;
		
		this.setCommand(Command.MOVE);
		this.requestBody.createMoveBody(matchID, sessionID, player, posX, posY, state, res);
		this.finalizeMessageObject();
	}
	
	public MoveClientMessage(String inputMessage) {
		super(inputMessage);
		
		this.matchID = this.requestBody.getBody().getInt("match_id");
		this.movePlayer = this.requestBody.getBody().getString("move_player");
		this.sessionID = this.requestBody.getBody().getString("session_id");
		JSONObject movePos = this.requestBody.getBody().getJSONObject("move_position");
		this.posX = movePos.getInt("x");
		this.posY = movePos.getInt("y");
		this.state = this.requestBody.getBody().getString("state");
		this.result = this.requestBody.getBody().getString("result");
	}	
	
	public int getMatchID() {
		return this.matchID;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
	
	public String getMovePlayer() {
		return this.movePlayer;
	}
	
	public int getX() {
		return this.posX;
	}
	
	public int getY() {
		return this.posY;
	}
	
	public String getState() {
		return this.state;
	}
	
	public String getResult() {
		return this.result;
	}
}
