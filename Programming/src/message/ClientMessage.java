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
		System.out.println("finalizeMessageObject:::: got here");
		this.finalMessageObject.put("command_code", this.command.toString());
		System.out.println("finalizeMessageObject:::: oneeeeeeeeeee");
		this.finalMessageObject.put("info", requestBody.getBody());
		System.out.println("finalizeMessageObject:::: twwwwwwwwoooooooooooooooooooooo");
		this.finalMessageObject.put("message_id", this.messageCommandID);
		System.out.println("finalizeMessageObject: done: " + this.finalMessageObject.toString());
	}
	
}
