package message.chat;

import message.ServerMessage;
import protocol.StatusCode;

public class ChatServerMessage extends ServerMessage {
	private String fromUser, toUser, message, messageID;
	private int matchID;
	
	public ChatServerMessage(String fromUsr, String toUsr, String msg, String msgID, int matchID, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		this.fromUser = fromUsr;
		this.toUser = toUsr;
		this.message = msg;
		this.messageID = msgID;
		this.matchID = matchID;
		
		this.responseBody.createChatBody();
		this.finalizeMessageObject();
	}
	
	public ChatServerMessage(String input) {
		super(input);
		
		this.fromUser = this.responseBody.getBody().getString("from_user");
		this.toUser = this.responseBody.getBody().getString("to_user");
		this.message = this.responseBody.getBody().getString("message");
		this.messageID = this.responseBody.getBody().getString("message_id");
		this.matchID = this.responseBody.getBody().getInt("match_id");
	}
	
	public String getSendUser() {
		return this.fromUser;
	}
	
	public String getReceiveUser() {
		return this.toUser;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String getMessageID() {
		return this.messageID;
	}
	
	public int getMatchID() {
		return this.matchID;
	}
}
