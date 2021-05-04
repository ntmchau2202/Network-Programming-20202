package protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseBody extends JSONObject {
	
	public ResponseBody() {
		super();
	}
	
	public void createLoginBody(String username, String sessionID, int elo) throws JSONException {
		this.clear();
		this.put("username", username);
		this.put("session_id", sessionID);
		this.put("elo", elo);		
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
