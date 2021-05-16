package protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseBody {
	
	private JSONObject responseBody;
	public ResponseBody() {
		this.responseBody = new JSONObject();
	}
	
	public void setBody(JSONObject body) {
		this.responseBody = body;
	}
	
	public Object getKey(String key) {
		return this.responseBody.get(key);
	}
	
	public JSONObject getBody() {
		return this.responseBody;
	}
	
	public void createLoginBody(String username, String sessionID, int elo, int rank, float winRate, 
			int numGamePlayed, int numGameWin) throws JSONException {
		this.responseBody.clear();
		this.responseBody.put("username", username);
		this.responseBody.put("session_id", sessionID);
		this.responseBody.put("elo", elo);
		this.responseBody.put("rank", rank);
		this.responseBody.put("win_rate",winRate);
		this.responseBody.put("num_game_played", numGamePlayed);
		this.responseBody.put("num_game_won", numGameWin);
		
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
