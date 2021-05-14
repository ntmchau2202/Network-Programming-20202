package protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestBody {
	
	private JSONObject body;
	
	public RequestBody() {
		body = new JSONObject();
	}
	
	public RequestBody(JSONObject obj) {
		body = obj;
	}
	
	public JSONObject getBody() {
		return this.body;
	}
	
	public void setBody(JSONObject body) {
		this.body = body;
	}
	
	public void createLoginBody(String username, String password) throws JSONException {
		this.body.clear();
		this.body.put("username", username);
		this.body.put("password", password);
	}
	
	public void createRegisterBody(String username, String password) throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
	
	public void createJoinQueueBody() throws JSONException {
		
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
	
	public void createLogoutBody() throws JSONException {
		
		// TODO: implement the info body of the corresponding message here
		
	}
}
