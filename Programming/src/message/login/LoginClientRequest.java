package message.login;

import message.Message;
import protocol.Command;
import protocol.RequestBody;

public class LoginClientRequest extends Message {

	private RequestBody requestInfo;
	
	public LoginClientRequest(String username, String password) throws Exception{
		super();
		this.requestInfo = new RequestBody();
		this.addCommandCode(Command.LOGIN);
		this.requestInfo.createLoginBody(username, password);
		this.addInfo(requestInfo.getBody());
	}
	
	public LoginClientRequest(String input) throws Exception {
		super(input);
		this.requestInfo = new RequestBody();
		this.requestInfo.setBody(message.getJSONObject("info"));
	}
	
	public String getUsername() throws Exception {
		return requestInfo.getBody().getString("username");
	}
	
	public String getPassword() throws Exception {
		return requestInfo.getBody().getString("password");
	}
}
