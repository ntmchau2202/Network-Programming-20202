package message.chatack;

import message.ClientMessage;
import protocol.StatusCode;

public class ChatACKClientMessage extends ClientMessage {
	private String messageID, errorMsg;
	private StatusCode statusCode;
	private int matchID;
	
	public ChatACKClientMessage(String messageID, int matchID, StatusCode statCode, String errMsg) {
		super();
		this.messageID = messageID;
		this.errorMsg = errMsg;
		this.statusCode = statCode;
		this.matchID = matchID;
		
		this.requestBody.createChatACKBody();
		this.finalizeMessageObject();
	}
	
	public ChatACKClientMessage(String input) {
		super(input);
		
		this.messageID = this.requestBody.getBody().getString("message_id");
		this.errorMsg = this.requestBody.getBody().getString("error");
		this.matchID = this.requestBody.getBody().getInt("match_id");
		this.statusCode = StatusCode.toStatusCode(this.requestBody.getBody().getString("status_code"));
	}
	
	public String getMessageID() {
		return this.messageID;
	}
	
	public String getErrorMessage() {
		return this.errorMsg;
	}
	
	public StatusCode getStatusCode() {
		return this.statusCode;
	}
	
	public int getMatchID() {
		return this.matchID;
	}
	
}
