package client.controller;

import client.network.ClientSocketChannel;
import entity.Player.RankPlayer;
import message.login.LoginServerMessage;
import protocol.StatusCode;

public class LoginFormController extends BaseController {
	private RankPlayer loggedPlayer;
    public boolean isLoginSuccessfully(String username, String password) throws Exception {
        String result = ClientSocketChannel.getSocketInstance().login(username, password);
        LoginServerMessage serverResponse = new LoginServerMessage(result);
        if (serverResponse.getStatusCode().compareTo(StatusCode.SUCCESS) == 0) {
        	String sessionID = serverResponse.getSessionID();
        	String returnedUsername = serverResponse.getUsername();
        	int elo = serverResponse.getELO();
        	loggedPlayer = new RankPlayer(returnedUsername, sessionID, elo);
        	return true;
        }
        
        return false;	
    }

    public RankPlayer getLoggedPlayer() {
        return loggedPlayer;
    }
}
