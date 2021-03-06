package message.login;

import message.ClientMessage;
import protocol.Command;

public class LoginClientMessage extends ClientMessage {
	
	private String username;
	private String password;
	
	public LoginClientMessage(String username, String password) {
		super();
		
		this.username = username;
		this.password = password;
		
		this.setCommand(Command.LOGIN);
		this.requestBody.createLoginBody(username, password);
		this.finalizeMessageObject();
	}
	
	public LoginClientMessage(String inputMessage) {
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
