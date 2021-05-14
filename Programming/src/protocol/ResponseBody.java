package protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseBody {
	private JSONObject body;
	
	public ResponseBody() {
		body = new JSONObject();
	}
	
	public ResponseBody(JSONObject obj) {
		body = obj;
	}
	
	public JSONObject getBody() {
		return this.body;
	}
	
	public void setBody(JSONObject body) {
		this.body = body;
	}
	
	public void createLoginBody(String username, String sessionID, int elo, int rank, float winRate, 
			int numGamePlayed, int numGameWin) throws JSONException {
		this.body.clear();
		this.body.put("username", username);
		this.body.put("session_id", sessionID);
		this.body.put("elo", elo);
		this.body.put("rank", rank);
		this.body.put("win_rate",winRate);
		this.body.put("num_game_played", numGamePlayed);
		this.body.put("num_game_win", numGameWin);
		
	}
	
	public void createRegisterBody(String username, String password) throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createJoinQueueBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createEndgameBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createMoveBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createDrawRequestBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createDrawConfirmBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createLeaderBoardBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createChatBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createChatACKBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
}
