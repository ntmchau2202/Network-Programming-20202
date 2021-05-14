package message;

import org.json.*;

import protocol.Command;
import protocol.RequestBody;

public abstract class Message {	
	protected Command commandCode;
	protected JSONObject message;
	
	protected Message() {
		message = new JSONObject();
	}
	
	protected Message(String input) {
		message = new JSONObject(input);
	}

	protected void addCommandCode(Command commandCode) throws JSONException {
		this.commandCode = commandCode;
		message.put("command_code", commandCode.toString());
	}
	
	protected void addInfo(JSONObject obj) {
		this.message.put("info", obj);
	}
	
	public String toString() {
		return message.toString();
	}
}
