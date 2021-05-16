package client.controller;

import client.entity.Player;
import client.network.ClientSocketChannel;
import helper.MessageParser;
import message.login.LoginServerMessage;
import protocol.StatusCode;

import java.io.IOException;

public class LoginFormController extends BaseController {
	
    public boolean isLoginSuccessfully(String username, String password) throws Exception {
        String result = ClientSocketChannel.getSocketInstance().login(username, password);
        LoginServerMessage serverResponse = new LoginServerMessage(result);
        if (serverResponse.getStatusCode().compareTo(StatusCode.SUCCESS) == 0) {
        	return true;
        }
        
        return false;
    }

    public Player getLoggedPlayer() {
        return new Player("hehe", "nani");
    }
}
