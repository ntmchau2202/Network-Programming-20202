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
	
	public void createRegisterRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createJoinQueueRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createMoveRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createDrawRequestRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createDrawConfirmRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createLeaderboardRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createChatRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createChatACKRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createLogoutRequest() throws JSONException {
		// TODO: create the body of the request here
	}
	
}
