package message.chat;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;
import server.entity.chat.ChatMessage;

public class ChatServerMessage extends ServerMessage {
	private String fromUser, toUser, message, messageID;
	private int matchID;

	public ChatServerMessage(ChatClientMessage chatClientMessage, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, chatClientMessage.getMessageCommandID());
		this.fromUser = chatClientMessage.getSendUser();
		this.toUser = chatClientMessage.getReceiveUser();
		this.message = chatClientMessage.getChatMessage();
		this.messageID = chatClientMessage.getChatMessageID();
		this.matchID = chatClientMessage.getMatchID();
		this.command = chatClientMessage.getCommand();
		this.responseBody.createChatBody(this.fromUser, this.toUser, this.message, this.messageID, matchID);
		this.finalizeMessageObject();
	}

	public ChatServerMessage(int messageCommandID, ChatMessage chatMessage, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, messageCommandID);
		this.fromUser = chatMessage.getSendPlayerName();
		this.toUser = chatMessage.getRecvPlayerName();
		this.message = chatMessage.getMessage();
		this.messageID = chatMessage.getMessageID();
		this.matchID = chatMessage.getMatchID();
		this.command = Command.CHAT;
		this.responseBody.createChatBody(this.fromUser, this.toUser, this.message, this.messageID, this.matchID);
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
	
	public String getChatMessage() {
		return this.message;
	}
	
	public String getChatMessageID() {
		return this.messageID;
	}
	
	public int getMatchID() {
		return this.matchID;
	}
}
