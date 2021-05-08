package protocol;


import org.json.JSONException;

public class ClientMessage extends Message {
	
	private RequestBody requestInfo;
	
	public ClientMessage() {
		super();
		requestInfo = new RequestBody();
	}
	
	private void finalizeRequest(Command cmd) throws JSONException {
		this.addCommandCode(cmd);
		this.addInfo(requestInfo);
	}
	
	public void createLoginRequest(String username, String password) throws JSONException {
		// first, create the info body
		requestInfo.createLoginBody(username, password);
		
		// then, let the function do the rest
		this.finalizeRequest(Command.LOGIN);
	}
	
	public void createRegisterRequest(String username, String password) throws JSONException {
		// first, create the info body
		requestInfo.createRegisterBody(username, password);

		// then, let the function do the rest
		this.finalizeRequest(Command.REGISTER);
	}
	
	public void createJoinQueueRequest(String mode, String session_id) throws JSONException {
		// first, create the info body
		requestInfo.createJoinQueueBody(mode, session_id);

		// then, let the function do the rest
		this.finalizeRequest(Command.JOIN_QUEUE);
	}
	
	public void createMoveRequest(String match_id, String session_id, String move_player, String x, String y, String state, String result) throws JSONException {
		// first, create the info body
		requestInfo.createMoveBody(match_id, session_id, move_player, x, y, state, result);

		// then, let the function do the rest
		this.finalizeRequest(Command.MOVE);
	}
	
	public void createDrawRequestRequest(String match_id, String session_id, String move_player) throws JSONException {
		// first, create the info body
		requestInfo.createDrawRequestBody(match_id, session_id, move_player);

		// then, let the function do the rest
		this.finalizeRequest(Command.DRAW_REQUEST);
	}
	
	public void createDrawConfirmRequest(String match_id, String session_id, String move_player, boolean acceptance) throws JSONException {
		// first, create the info body
		requestInfo.createDrawConfirmBody(match_id, session_id, move_player, acceptance);

		// then, let the function do the rest
		this.finalizeRequest(Command.DRAW_CONFIRM);
	}
	
	public void createLeaderboardRequest(String session_id, String username) throws JSONException {
		// first, create the info body
		requestInfo.createLeaderBoardBody(session_id, username);

		// then, let the function do the rest
		this.finalizeRequest(Command.LEADERBOARD);
	}
	
	public void createChatRequest(String from_user, String to_user, String message, String message_id, String match_id) throws JSONException {
		// first, create the info body
		requestInfo.createChatBody(from_user, to_user, message, message_id, match_id);

		// then, let the function do the rest
		this.finalizeRequest(Command.CHAT);
	}
	
	public void createChatACKRequest(String message_id, String match_id, StatusCode status_code ,String error) throws JSONException {
		// first, create the info body
		requestInfo.createChatACKBody(message_id, match_id, status_code, error);

		// then, let the function do the rest
		this.finalizeRequest(Command.CHATACK);
	}
	
	public void createLogoutRequest(String username, String session_id) throws JSONException {
		// first, create the info body
		requestInfo.createLogoutBody(username, session_id);

		// then, let the function do the rest
		this.finalizeRequest(Command.LOGOUT);
	}
	
}
