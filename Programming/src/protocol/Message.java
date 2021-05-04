package protocol;

import org.json.*;

public abstract class Message {	
	protected JSONObject messageBody;
	
	protected Message() {
		messageBody = new JSONObject();
	}
	
	protected void addCommandCode(Command command_code) throws JSONException {
		// check if the key exists and remove before modify
		if ( messageBody.has("command_code") ) {
			messageBody.remove("command_code");
		}
		messageBody.put("command_code", command_code);
	}
	
	protected void addInfo(JSONObject objInfo) {
		// check if the key exists and remove before modify
		if ( messageBody.has("info") ) {
			messageBody.remove("info");
		}
		messageBody.put("info", objInfo);
	}

	
	public String toString() {
		return messageBody.toString();
	}
}
