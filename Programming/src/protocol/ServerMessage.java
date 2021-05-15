package protocol;

import org.json.JSONArray;
import org.json.JSONException;

public class ServerMessage extends Message {
	
	private ResponseBody responseInfo;
	
	public ServerMessage() {
		super();
		responseInfo = new ResponseBody();
	}
	
	private void addStatusCode(StatusCode statCode) throws JSONException {
		// check if the key exists and remove before modify
		if ( messageBody.has("status_code") ) {
			messageBody.remove("status_code");
		}
		
		messageBody.put("status_code", statCode.getStatusCodeString());
		
	}
	
	private void addErrorDetails(String errDetails) throws JSONException {
		// check if the key exists and remove before modify
		if ( messageBody.has("error") ) {
			messageBody.remove("error");
		}
		messageBody.put("error", errDetails);
	}
	
	private void finalizeResponse(Command cmd, StatusCode statCode, String errDetails) throws JSONException {
		this.addCommandCode(cmd);
		this.addInfo(responseInfo);
		this.addStatusCode(statCode);
		this.addErrorDetails(errDetails);
	}
	
	public void createLoginResponse(String username, String sessionID, int elo, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createLoginBody(username, sessionID, elo);
	
		// then, let the function do the rest
		this.finalizeResponse(Command.LOGIN, statCode, errDetails);
	}
	
	public void createRegisterResponse(String username, String sessionID, int elo, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createRegisterBody(username, sessionID, elo);

		// then, let the function do the rest
		this.finalizeResponse(Command.REGISTER, statCode, errDetails);
	}
	
	public void createJoinQueueResponse(String username, String sessionID, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createJoinQueueBody(username, sessionID);

		// then, let the function do the rest
		this.finalizeResponse(Command.JOIN_QUEUE, statCode, errDetails);
	}
	
	public void createMatchFoundResponse(String match_id, String opponent, int elo, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createMatchFoundBody(match_id, opponent, elo);

		// then, let the function do the rest
		this.finalizeResponse(Command.MATCH_FOUND, statCode, errDetails);
	}

	public void createMoveResponse(String match_id, String move_player, String x, String y, String state, String result, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createMoveBody(match_id, move_player, x, y, state, result);

		// then, let the function do the rest
		this.finalizeResponse(Command.MOVE, statCode, errDetails);
	}

	public void createEndgameResponse(String match_id, String winner_username, int winner_elo, String loser_username, int loser_elo, boolean draw, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createEndgameBody(match_id, winner_username, winner_elo, loser_username, loser_elo, draw);

		// then, let the function do the rest
		this.finalizeResponse(Command.ENDGAME, statCode, errDetails);
	}

	public void createDrawRequestResponse(String match_id, String move_player, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createDrawRequestBody(match_id, move_player);

		// then, let the function do the rest
		this.finalizeResponse(Command.DRAW_REQUEST, statCode, errDetails);
	}
	
	public void createDrawConfirmResponse(String match_id, String move_player, boolean acceptance, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createDrawConfirmBody(match_id, move_player, acceptance);

		// then, let the function do the rest
		this.finalizeResponse(Command.DRAW_CONFIRM, statCode, errDetails);
	}

	public void createLeaderboardResponse(JSONArray username, JSONArray elo, JSONArray rank, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createLeaderBoardBody(username, elo, rank);

		// then, let the function do the rest
		this.finalizeResponse(Command.LEADERBOARD, statCode, errDetails);
	}
	
	public void createChatResponse(String from_user, String to_user, String message, String message_id, String match_id, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createChatBody(from_user, to_user, message, message_id, match_id);

		// then, let the function do the rest
		this.finalizeResponse(Command.CHAT, statCode, errDetails);
	}
	
	public void createChatACKResponse(String message_id, String match_id, StatusCode statCode, String errDetails) throws JSONException {
		// first, create the info body
		this.responseInfo.createChatACKBody(message_id, match_id);

		// then, let the function do the rest
		this.finalizeResponse(Command.CHATACK, statCode, errDetails);
	}
	
}
