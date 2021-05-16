package protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseBody extends JSONObject {
	
	public ResponseBody() {
		super();
	}
	
	public void createLoginBody(String username, String sessionID, int elo) throws JSONException {
		this.clear();
		this.put("session_id", sessionID);
		this.put("username", username);
		this.put("elo", elo);
	}
	
	public void createRegisterBody(String username, String sessionID, int elo) throws JSONException {
		this.clear();
		this.put("session_id", sessionID);
		this.put("username", username);
		this.put("elo", elo);
	}
	
	public void createJoinQueueBody(String username, String sessionID) throws JSONException {
		this.clear();
		this.put("session_id", sessionID);
		this.put("username", username);
	}

	public void createMatchFoundBody(String match_id, String opponent, int elo) throws JSONException {
		this.clear();
		this.put("match_id", match_id);
		this.put("opponent", opponent);
		this.put("elo", elo);
	}


	public void createMoveBody(String match_id, String move_player, String x, String y, String state, String result) throws JSONException {
		this.clear();
		JSONObject move_position = new JSONObject();
		move_position.put("x", x);
		move_position.put("y", y);
		this.put("match_id", match_id);
		this.put("move_player", move_player);
		this.put("move_position", move_position);
		this.put("state", state);
		this.put("result", result);
	}

	public void createEndgameBody(String match_id, String winner_username, int winner_elo, String loser_username, int loser_elo, boolean draw) throws JSONException {
		this.clear();
		JSONObject winner = new JSONObject();
		JSONObject loser = new JSONObject();
		winner.put("username", winner_username);
		winner.put("elo", winner_elo);
		loser.put("username", loser_username);
		loser.put("elo", loser_elo);
		this.put("match_id", match_id);
		this.put("winner", winner);
		this.put("loser", loser);
		this.put("draw", draw);
	}

	
	public void createDrawRequestBody(String match_id, String move_player) throws JSONException {
		this.clear();
		this.put("match_id", match_id);
		this.put("move_player", move_player);
	}
	
	public void createDrawConfirmBody(String match_id, String move_player, boolean acceptance) throws JSONException {
		this.clear();
		this.put("match_id", match_id);
		this.put("move_player", move_player);
		this.put("acceptance", acceptance);
	}
	
	public void createLeaderBoardBody(JSONArray username, JSONArray elo, JSONArray rank) throws JSONException {
		this.clear();
		this.put("username", username);
		this.put("elo", elo);
		this.put("rank", rank);
	}
	
	public void createChatBody(String from_user, String to_user, String message, String message_id, String match_id) throws JSONException {
		this.clear();
		this.put("from_user", from_user);
		this.put("to_user", to_user);
		this.put("message", message);
		this.put("message_id", message_id);
		this.put("match_id", match_id);
	}
	
	public void createChatACKBody(String message_id, String match_id) throws JSONException {
		this.clear();
		this.put("message_id", message_id);
		this.put("match_id", match_id);
	}
}
