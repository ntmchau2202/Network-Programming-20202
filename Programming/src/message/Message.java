package message;

import java.util.Random;

import org.json.JSONObject;

import protocol.Command;

public abstract class Message {
	protected Command command;
	protected JSONObject finalMessageObject;
	protected int messageCommandID;
	
	public Message() {
		this.finalMessageObject = new JSONObject();
		Random rn = new Random();
		this.messageCommandID = rn.nextInt(1000000);
	}
	
	public Message(String input) {
		this.finalMessageObject = new JSONObject(input);
		this.command = Command.toCommand(this.finalMessageObject.getString("command_code"));
	}
	
	protected void setCommand(Command cmd) {
		this.command = cmd;
	}
	// classes must override this
	protected void finalizeMessageObject() {
		
	}
	
	public Command getCommand() {
		return this.command;
	}
	
	public String toString() {
		return finalMessageObject.toString();
	}
	
	public int getMessageCommandID() {
		return this.messageCommandID;
	}
}
