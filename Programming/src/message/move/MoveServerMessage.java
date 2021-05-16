package message.move;

import org.json.JSONObject;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class MoveServerMessage extends ServerMessage{

	private int matchID, posX, posY;
	private String sessionID, movePlayer, state, result;
	
	public MoveServerMessage(int matchID, String player, String sessionID, int posX, int posY, String state, String res, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.sessionID = sessionID;
		this.posX = posX;
		this.posY = posY;
		this.state = state;
		this.result = res;
		
		this.setCommand(Command.MOVE);
		this.responseBody.createMoveBody();
		this.finalizeMessageObject();
	}
	
	public MoveServerMessage(String input) {
		super(input);
		
		this.matchID = (int)this.responseBody.getKey("match_id");
		this.movePlayer = (String)this.responseBody.getKey("move_player");
		this.sessionID = (String)this.responseBody.getKey("session_id");
		JSONObject movePos = (JSONObject)this.responseBody.getKey("move_position");
		this.posX = movePos.getInt("x");
		this.posY = movePos.getInt("y");
		this.state = (String)this.responseBody.getKey("state");
		this.result = (String)this.responseBody.getKey("result");
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
