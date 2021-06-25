package client.controller;

import client.network.ClientSocketChannel;
import message.logout.LogoutServerMessage;
import protocol.StatusCode;

public class BaseController {
	public boolean logout(String username, String sesID) throws Exception {
		String result = ClientSocketChannel.getSocketInstance().logout(username, sesID);
		LogoutServerMessage msg = new LogoutServerMessage(result);
		if(msg.getStatusCode().compareTo(StatusCode.ERROR)==0) {
			return false;
		} else {
			return true;
		}
	}
}
