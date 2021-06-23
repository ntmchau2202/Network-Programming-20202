package client.controller;

import client.network.ClientSocketChannel;

public class BaseController {
	public void logout(String username, String sesID) throws Exception {
		ClientSocketChannel.getSocketInstance().logout(username, sesID);
	}
}
