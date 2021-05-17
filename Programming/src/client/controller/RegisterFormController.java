package client.controller;

import client.network.ClientSocketChannel;
import entity.Player.RankPlayer;
import message.register.RegisterServerMessage;
import protocol.StatusCode;

public class RegisterFormController extends BaseController {
    private RankPlayer loggedPlayer;

    public boolean isRegisterSuccessfully(String username, String password) throws Exception {
        String result = ClientSocketChannel.getSocketInstance().register(username, password);
        // parsing stuff here
        RegisterServerMessage serverResponse = new RegisterServerMessage(result);
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
