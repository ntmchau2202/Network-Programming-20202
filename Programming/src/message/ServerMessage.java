package message;

import protocol.ResponseBody;
import protocol.StatusCode;

public abstract class ServerMessage extends Message {
	protected ResponseBody responseBody;
	protected StatusCode statusCode;
	protected String errorMsg;
	
	public ServerMessage(StatusCode statCode, String errMsg) {
		super();
		this.responseBody = new ResponseBody();
		this.statusCode = statCode;
		this.errorMsg = errMsg;
	}
	
	public ServerMessage(String input) {
		super(input);
		this.responseBody = new ResponseBody();
		this.responseBody.setBody(this.finalMessageObject.getJSONObject("info"));
		
		this.statusCode = StatusCode.toStatusCode(this.finalMessageObject.getString("status_code"));
		this.errorMsg = this.finalMessageObject.getString("error");
		
	}
	
	protected void finalizeMessageObject() {
		this.finalMessageObject.put("command_code", this.command.toString());
		this.finalMessageObject.put("info", responseBody.getBody());
		this.finalMessageObject.put("status_code", statusCode.toString());
		this.finalMessageObject.put("error", errorMsg);
		this.finalMessageObject.put("message_id", this.messageID);
	}
	
	public StatusCode getStatusCode() {
		return this.statusCode;
	}
	
	public String getErrorMessage() {
		return this.errorMsg;
	}
	
	public void changeStatusCode(StatusCode newStatusCode) {
		this.statusCode = newStatusCode;
	}
	
	public void changeErrorMessage(String newErrMsg) {
		this.errorMsg = newErrMsg;
	}
}
