package helper;

import org.json.JSONObject;

import protocol.Command;
import protocol.StatusCode;

import org.json.JSONException;

public class MessageParser {
	public MessageParser() {
		super();
	}
	
	public Command getCommand(String inputMessage) throws JSONException {
		JSONObject inputJSONMsg = new JSONObject(inputMessage);
		String strResultCommand = "";
		if (inputJSONMsg.has("command_code")){
			strResultCommand = inputJSONMsg.getString("command_code");
		}		

		return Command.toCommand(strResultCommand);
	}
	
	public StatusCode getStatusCode(String inputMessage) throws JSONException {
		JSONObject inputJSONMsg = new JSONObject(inputMessage);
		String strResultStatusCode = "";
		if (inputJSONMsg.has("status_code")){
			strResultStatusCode = inputJSONMsg.getString("status_code");
		}		
		return StatusCode.toStatusCode(strResultStatusCode);
	}
	
	public String getErrorMessage(String inputMessage) throws JSONException {
		JSONObject inputJSONMsg = new JSONObject(inputMessage);
		String resultError = "";
		if (inputJSONMsg.has("error")){
			resultError = inputJSONMsg.getString("error");
		}		
		return resultError;
	}
	
	public Object getInfoField(String inputMessage, String key) throws JSONException {
		JSONObject inputJSONMsg = new JSONObject(inputMessage);
		Object infoField = null;
		if(inputJSONMsg.has("info")) {
			JSONObject info = inputJSONMsg.getJSONObject("info");
			if (info.has(key)) {
				infoField = info.get(key);
			}
		}
		return infoField;
	}
	
}
