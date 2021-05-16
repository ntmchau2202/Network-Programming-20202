package message;

import org.json.JSONObject;

import protocol.Command;

public abstract class Message {
	protected Command command;
	protected JSONObject finalMessageObject;
	
	public Message() {
		this.finalMessageObject = new JSONObject();
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
}
