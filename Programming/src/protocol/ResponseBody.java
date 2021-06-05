package protocol;

import org.json.JSONArray;
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
		this.responseBody.put("win_rate", String.valueOf(winRate));
		this.responseBody.put("num_game_played", numGamePlayed);
		this.responseBody.put("num_game_won", numGameWin);
	}
		
	
	public void createRegisterBody(String username, String sessionID, int elo) throws JSONException {
		this.responseBody.clear();
		this.responseBody.put("session_id", sessionID);
		this.responseBody.put("username", username);
		this.responseBody.put("elo", elo);
	}
	
	public void createJoinQueueBody(String username, String sessionID, String opponent, int elo, int matchID, String player1) throws JSONException {
		this.responseBody.clear();
		this.responseBody.put("session_id", sessionID);
		this.responseBody.put("username", username);
		this.responseBody.put("match_id", matchID);
		this.responseBody.put("opponent", opponent);
		this.responseBody.put("opponent_elo", elo);
		this.responseBody.put("elo", elo);
		this.responseBody.put("player_1", player1);
	}
	
	public void createQuitQueueBody(String username) {
		this.responseBody.clear();
		this.responseBody.put("username", username);
	}

	public void createMatchFoundBody(int matchID, String opponent, int elo, String firstPlayer) {
		this.responseBody.put("match_id", matchID);
		this.responseBody.put("opponent", opponent);
		this.responseBody.put("elo", elo);
		this.responseBody.put("player_1", firstPlayer);
	}

	public void createMoveBody(int match_id, String move_player, int x, int y, String state, String result) throws JSONException {
		this.responseBody.clear();
		JSONObject move_position = new JSONObject();
		move_position.put("x", x);
		move_position.put("y", y);
		this.responseBody.put("match_id", match_id);
		this.responseBody.put("move_player", move_player);
		this.responseBody.put("move_position", move_position);
		this.responseBody.put("state", state);
		this.responseBody.put("result", result);
	}

	public void createEndgameBody(int match_id, String winner_username, int winner_elo, String loser_username, int loser_elo, boolean draw) throws JSONException {
		this.responseBody.clear();
		JSONObject winner = new JSONObject();
		JSONObject loser = new JSONObject();
		winner.put("username", winner_username);
		winner.put("elo", winner_elo);
		loser.put("username", loser_username);
		loser.put("elo", loser_elo);
		this.responseBody.put("match_id", match_id);
		this.responseBody.put("winner", winner);
		this.responseBody.put("loser", loser);
		this.responseBody.put("draw", draw);
	}

	
	public void createDrawRequestBody(int match_id, String move_player) throws JSONException {
		this.responseBody.clear();
		this.responseBody.put("match_id", match_id);
		this.responseBody.put("move_player", move_player);
	}
	
	public void createDrawConfirmBody(int match_id, String move_player, boolean acceptance) throws JSONException {
		this.responseBody.clear();
		this.responseBody.put("match_id", match_id);
		this.responseBody.put("move_player", move_player);
		this.responseBody.put("acceptance", acceptance);
	}
	
	public void createLeaderBoardBody(JSONArray username, JSONArray elo, JSONArray rank) throws JSONException {
		this.responseBody.clear();
		this.responseBody.put("username", username);
		this.responseBody.put("elo", elo);
		this.responseBody.put("rank", rank);
	}
	
	public void createChatBody(String from_user, String to_user, String message, String message_id, int match_id) throws JSONException {
		this.responseBody.clear();
		this.responseBody.put("from_user", from_user);
		this.responseBody.put("to_user", to_user);
		this.responseBody.put("message", message);
		this.responseBody.put("message_id", message_id);
		this.responseBody.put("match_id", match_id);
	}
	
	public void createChatACKBody(String message_id, int match_id) throws JSONException {
		this.responseBody.clear();
		this.responseBody.put("message_id", message_id);
		this.responseBody.put("match_id", match_id);
	}
}
