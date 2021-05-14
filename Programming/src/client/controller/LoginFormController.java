package client.controller;

import client.entity.Player;
import client.network.ClientSocketChannel;
import helper.MessageParser;
import message.login.LoginServerResponse;
import protocol.ClientMessage;
import protocol.StatusCode;

import java.io.IOException;

public class LoginFormController extends BaseController {
	
	// private MessageParser msgParser = new MessageParser();
	
    public boolean isLoginSuccessfully(String username, String password) throws Exception {
        String result = ClientSocketChannel.getSocketInstance().login(username, password);
        // parsing stuff here
//        if (result.length() != 0) {
//        	StatusCode stat = msgParser.getStatusCode(result);
//	        if (stat.compareTo(StatusCode.SUCCESS) == 0) {
//	        	return true;
//	        } 
//        }
//        return false;
        LoginServerResponse serverResponse = new LoginServerResponse(result);
        if (serverResponse.getStatusCode().compareTo(StatusCode.SUCCESS) == 0) {
        	return true;
        }
        
        return false;
    }

    public Player getLoggedPlayer() {
        return new Player("hehe", "nani");
    }
}
