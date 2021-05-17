package message.register;

import message.ClientMessage;
import protocol.Command;

public class RegisterClientMessage extends ClientMessage {
	private String username;
	private String password;
	
	public RegisterClientMessage(String username, String password) {
		super();
		
		this.username = username;
		this.password = password;
		
		this.setCommand(Command.REGISTER);
		this.requestBody.createRegisterBody(username, password);
		this.finalizeMessageObject();
	}
	
	public RegisterClientMessage(String inputMessage) {
		super(inputMessage);

		this.username = this.requestBody.getBody().getString("username");
		this.password = this.requestBody.getBody().getString("password");
	}	
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
}
