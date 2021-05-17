package message.chat;

import message.ClientMessage;

public class ChatClientMessage extends ClientMessage {
	private String fromUser, toUser, message, messageID;
	private int matchID;
	
	public ChatClientMessage(String fromUsr, String toUsr, String msg, String msgID, int matchID) {
		super();
		this.fromUser = fromUsr;
		this.toUser = toUsr;
		this.message = msg;
		this.messageID = msgID;
		this.matchID = matchID;
		
		this.requestBody.createChatBody(fromUsr, toUsr, msg, msgID, matchID);
		this.finalizeMessageObject();
	}
	
	public ChatClientMessage(String input) {
		super(input);
		
		this.fromUser = this.requestBody.getBody().getString("from_user");
		this.toUser = this.requestBody.getBody().getString("to_user");
		this.message = this.requestBody.getBody().getString("message");
		this.messageID = this.requestBody.getBody().getString("message_id");
		this.matchID = this.requestBody.getBody().getInt("match_id");
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
