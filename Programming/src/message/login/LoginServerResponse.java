package message.login;

import org.json.JSONObject;

import message.Message;
import protocol.Command;
import protocol.ResponseBody;
import protocol.StatusCode;

public class LoginServerResponse extends Message {
	private ResponseBody responseInfo;
	private StatusCode statusCode;
	private String error;
	
	public LoginServerResponse(String username, String sessionID, int elo, int rank, float winRate, 
			int numGamePlayed, int numGameWin, StatusCode statCode, String error) {
		super();
		this.responseInfo = new ResponseBody();
		this.addCommandCode(Command.LOGIN);
		this.statusCode = statCode;
		this.error = error;
		this.responseInfo.createLoginBody(username, sessionID, elo, rank, winRate, numGamePlayed, numGameWin);
		this.addInfo(responseInfo.getBody());
	}
	
	public LoginServerResponse(String input) {
		super(input);
		JSONObject obj = new JSONObject(input);
		this.responseInfo = new ResponseBody(message.getJSONObject("info"));
		this.statusCode = StatusCode.toStatusCode(obj.getString("status_code"));
		this.error = obj.getString("error");
	}
	
	public String getUsername() {
		return responseInfo.getBody().getString("username");
	}
	
	public String getSessionID() {
		return responseInfo.getBody().getString("session_id");
	}
	
	public int getELO() {
		return responseInfo.getBody().getInt("elo");
	}
	
	public int getRank() {
		return responseInfo.getBody().getInt("rank");
	}
	
	public float getWinRate() {
		return responseInfo.getBody().getFloat("win_rate");
	}
	
	public int getNumberOfGamePlayed() {
		return responseInfo.getBody().getInt("num_game_played");
	}
	
	public int getNumberOfGameWin() {
		return responseInfo.getBody().getInt("num_game_win");
	}
	
	public String getError() {
		return this.error;
	}
	
	public StatusCode getStatusCode() {
		return this.statusCode;
	}
	
	public String toString() {
		message.put("status_code", statusCode.toString());
		message.put("error", error);
		return message.toString();
	}
}
