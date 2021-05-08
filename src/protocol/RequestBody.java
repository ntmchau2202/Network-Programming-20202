package protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestBody extends JSONObject {
	
	public RequestBody() {
		super();
	}
	
	public void createLoginBody(String username, String password) throws JSONException {
		this.clear();
		this.put("username", username);
		this.put("password", password);
	}
	
	public void createRegisterBody(String username, String password) throws JSONException {
		this.clear();
		this.put("username", username);
		this.put("password", password);
	}
	
	public void createJoinQueueBody(String mode, String session_id) throws JSONException {
		this.clear();
		this.put("mode", mode);
		this.put("session_id", session_id);
	}
	
	public void createMoveBody(String match_id, String session_id, String move_player, String x, String y, String state, String result) throws JSONException {
		this.clear();
		JSONObject move_position = new JSONObject();
		move_position.put("x", x);
		move_position.put("y", y);
		this.put("match_id", match_id);
		this.put("session_id", session_id);
		this.put("move_player", move_player);
		this.put("move_position", move_position);
		this.put("state", state);
		this.put("result", result);
	}
	
	public void createDrawRequestBody(String match_id, String session_id, String move_player) throws JSONException {
		this.clear();
		this.put("match_id", match_id);
		this.put("session_id", session_id);
		this.put("move_player", move_player);
	}
	
	public void createDrawConfirmBody(String match_id, String session_id, String move_player, boolean acceptance) throws JSONException {
		this.clear();
		this.put("match_id", match_id);
		this.put("session_id", session_id);
		this.put("move_player", move_player);
		this.put("acceptance", acceptance);
	}
	
	public void createLeaderBoardBody(String session_id, String username) throws JSONException {
		this.clear();
		this.put("session_id", session_id);
		this.put("username", username);
	}
	
	public void createChatBody(String from_user, String to_user, String message, String message_id, String match_id) throws JSONException {
		this.clear();
		this.put("from_user", from_user);
		this.put("to_user", to_user);
		this.put("message", message);
		this.put("message_id", message_id);
		this.put("match_id", match_id);
	}
	
	public void createChatACKBody(String message_id, String match_id, StatusCode status_code ,String error) throws JSONException {
		this.clear();
		this.put("message_id", message_id);
		this.put("match_id", match_id);
		this.put("status_code", status_code.getStatusCodeString());
		this.put("error", error);
	}
	
	public void createLogoutBody(String username, String session_id) throws JSONException {
		this.clear();
		this.put("username", username);
		this.put("session_id", session_id);
	}
}
