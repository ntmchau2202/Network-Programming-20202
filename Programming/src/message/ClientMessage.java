package message;

import protocol.RequestBody;

public abstract class ClientMessage extends Message {
	protected RequestBody requestBody;
	
	protected ClientMessage() {
		super();
		this.requestBody = new RequestBody();
	}
	
	protected ClientMessage(String inputMsg) {
		super(inputMsg);
		this.requestBody = new RequestBody();
		this.requestBody.setBody(this.finalMessageObject.getJSONObject("info"));
	}
	
	protected void finalizeMessageObject() {
		this.finalMessageObject.put("command_code", this.command.toString());
		this.finalMessageObject.put("info", requestBody.getBody());
	}
	
}
