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
	
	public Object getKey(String key) {
		return this.requestBody.get(key);
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
