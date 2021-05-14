package client.controller;

import client.entity.Player;
import client.network.ClientSocketChannel;
import helper.MessageParser;
import protocol.ClientMessage;
import protocol.StatusCode;

import java.io.IOException;

public class LoginFormController extends BaseController {
	
	private MessageParser msgParser = new MessageParser();
	private Player loggedPlayer;
	
    public boolean isLoginSuccessfully(String username, String password) throws Exception {
        String result = ClientSocketChannel.getSocketInstance().login(username, password);
        // parsing stuff here
        if (result.length() != 0) {
        	StatusCode stat = msgParser.getStatusCode(result);
	        if (stat.compareTo(StatusCode.SUCCESS) == 0) {
                String sessionID = String.valueOf(msgParser.getInfoField(result,"session_id"));
                int elo = (int)msgParser.getInfoField(result, "elo");
	            loggedPlayer = new Player(username, sessionID, elo);

	        	return true;
	        } 
        }
        return false;
    }

    public Player getLoggedPlayer() {
        return loggedPlayer;
    }
}
