package protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestBody {
	
	private JSONObject requestBody;
	
	public RequestBody() {
		this.requestBody = new JSONObject();
	}
	
	public void setBody(JSONObject body) {
		this.requestBody = body;
	}
	
	public JSONObject getBody() {
		return this.requestBody;
	}
	
	public void createLoginBody(String username, String password) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("username", username);
		this.requestBody.put("password", password);
	}
	
	public void createRegisterBody(String username, String password) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("username", username);
		this.requestBody.put("password", password);
	}
	
	public void createUpdateUserBody(String username, String sessionID) {
		this.requestBody.clear();
		this.requestBody.put("username", username);
		this.requestBody.put("session_id", sessionID);
	}
	
	public void createJoinQueueBody(String mode, String session_id) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("mode", mode);
		this.requestBody.put("session_id", session_id);
	}
	
	public void createQuitQueueBody(String username, String sesID) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("username", username);
		this.requestBody.put("session_id", sesID);
	}
	
	
	public void createMoveBody(int match_id, String session_id, String move_player, int x, int y, String state, String result) throws JSONException {
		this.requestBody.clear();
		JSONObject move_position = new JSONObject();
		move_position.put("x", x);
		move_position.put("y", y);
		this.requestBody.put("match_id", match_id);
		this.requestBody.put("session_id", session_id);
		this.requestBody.put("move_player", move_player);
		this.requestBody.put("move_position", move_position);
		this.requestBody.put("state", state);
		this.requestBody.put("result", result);
	}
	
	public void createListenMoveBody(String username, int matchID) {
		this.requestBody.clear();
		this.requestBody.put("username", username);
		this.requestBody.put("match_id", matchID);
	}
	
	public void createDrawRequestBody(int match_id, String session_id, String move_player) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("match_id", match_id);
		this.requestBody.put("session_id", session_id);
		this.requestBody.put("move_player", move_player);
	}
	
	public void createListenDrawRequestBody(String username, String session_id, int matchID) {
		this.requestBody.clear();
		this.requestBody.put("username", username);
		this.requestBody.put("session_id", session_id);
		this.requestBody.put("match_id", matchID);
	}
	
	public void createDrawConfirmBody(int match_id, String session_id, String move_player, boolean acceptance) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("match_id", match_id);
		this.requestBody.put("session_id", session_id);
		this.requestBody.put("move_player", move_player);
		this.requestBody.put("acceptance", acceptance);
	}
	
	public void createLeaderBoardBody(String session_id, String username) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("session_id", session_id);
		this.requestBody.put("username", username);
	}
	
	public void createChatBody(String from_user, String to_user, String message, String message_id, int match_id) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("from_user", from_user);
		this.requestBody.put("to_user", to_user);
		this.requestBody.put("message", message);
		this.requestBody.put("message_id", message_id);
		this.requestBody.put("match_id", match_id);
	}
	
	public void createChatACKBody(String message_id, int match_id, StatusCode status_code ,String error) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("message_id", message_id);
		this.requestBody.put("match_id", match_id);
		this.requestBody.put("status_code", status_code.getStatusCodeString());
		this.requestBody.put("error", error);
	}
	
	public void createLogoutBody(String username, String session_id) throws JSONException {
		this.requestBody.clear();
		this.requestBody.put("username", username);
		this.requestBody.put("session_id", session_id);
	}
	
	public void createQuitGameBody(int matchID, String username, String sessionID) {
		this.requestBody.clear();
		this.requestBody.put("match_id", matchID);
		this.requestBody.put("username", username);
		this.requestBody.put("session_id", sessionID);
	}
}
