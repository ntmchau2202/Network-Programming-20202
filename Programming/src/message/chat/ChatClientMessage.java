package message.chat;

import java.util.Random;

import message.ClientMessage;
import protocol.Command;

public class ChatClientMessage extends ClientMessage {
	private String fromUser, toUser, message, messageID;
	private int matchID;
	
	public ChatClientMessage(String fromUsr, String toUsr, String msg, int matchID) {
		super();
		this.fromUser = fromUsr;
		this.toUser = toUsr;
		this.message = msg;
		Random rn = new Random();
		int idNum = rn.nextInt(10000);
		this.messageID = "CHAT" + Integer.toString(idNum);
		this.matchID = matchID;
		this.setCommand(Command.CHAT);
		this.requestBody.createChatBody(fromUsr, toUsr, msg, this.messageID, matchID);
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
