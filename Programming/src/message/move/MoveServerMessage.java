package message.move;

import org.json.JSONObject;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class MoveServerMessage extends ServerMessage{

	private int matchID, posX, posY;
	private String movePlayer, state, result;
	
	public MoveServerMessage(int messageCommandID, int matchID, String player, int posX, int posY, String state, String res, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, messageCommandID);
		
		this.matchID = matchID;
		this.movePlayer = player;
		this.posX = posX;
		this.posY = posY;
		this.state = state;
		this.result = res;
		
		this.setCommand(Command.MOVE);
		this.responseBody.createMoveBody(matchID, player, posX, posY, state, res);
		this.finalizeMessageObject();
	}
	
	public MoveServerMessage(String input) {
		super(input);
		
		this.matchID = this.responseBody.getBody().getInt("match_id");
		this.movePlayer = this.responseBody.getBody().getString("move_player");
		JSONObject movePos = this.responseBody.getBody().getJSONObject("move_position");
		this.posX = movePos.getInt("x");
		this.posY = movePos.getInt("y");
		this.state = this.responseBody.getBody().getString("state");
		this.result = (String)this.responseBody.getBody().getString("result");
	}
	
	public int getMatchID() {
		return this.matchID;
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
