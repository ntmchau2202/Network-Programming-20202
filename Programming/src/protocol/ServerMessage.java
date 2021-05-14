package protocol;

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
		// this.responseInfo.createLoginBody(username, sessionID, elo);
	
		// then, let the function do the rest
		this.finalizeResponse(Command.LOGIN, statCode, errDetails);
	}
	
	public void createRegisterResponse() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createJoinQueueResponse() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createMatchFoundRespone() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createDrawRequestResponse() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createDrawConfirmResponse() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createEndgameResponse() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createLeaderboardResponse() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createChatResponse() throws JSONException {
		// TODO: create the body of the request here
	}
	
	public void createChatACKResponse() throws JSONException {
		// TODO: create the body of the request here
	}
	
}
