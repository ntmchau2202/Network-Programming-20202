package message.chatack;

import message.ServerMessage;
import protocol.StatusCode;

public class ChatACKServerMessage extends ServerMessage {
	private String messageID, errorMsg;
	private StatusCode statusCode;
	private int matchID;
	
	public ChatACKServerMessage(String messageID, int matchID, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, );
		this.messageID = messageID;
		this.errorMsg = errMsg;
		this.statusCode = statCode;
		this.matchID = matchID;
		
		this.responseBody.createChatACKBody(messageID, matchID);
		this.finalizeMessageObject();
	}
	
	public ChatACKServerMessage(String input) {
		super(input);
		
		this.messageID = this.responseBody.getBody().getString("message_id");
		this.errorMsg = this.responseBody.getBody().getString("error");
		this.matchID = this.responseBody.getBody().getInt("match_id");
		this.statusCode = StatusCode.toStatusCode(this.responseBody.getBody().getString("status_code"));
	}
	
	public String getChatMessageID() {
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
