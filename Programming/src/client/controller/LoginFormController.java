package client.controller;

import client.entity.Player;
import client.network.ClientSocketChannel;
import protocol.ClientMessage;

import java.io.IOException;

public class LoginFormController extends BaseController {
    public boolean isLoginSuccessfully(String username, String password) throws IOException {
        String loginMsg = getLoginMessage(username, password);
        return interactWithServer(loginMsg);
    }

    public Player getLoggedPlayer() {
        return new Player("hehe", "nani");
    }

    private String getLoginMessage(String username, String password) {
        ClientMessage loginRequest = new ClientMessage();
        loginRequest.createLoginRequest(username, password);
        return loginRequest.toString();
    }

    private boolean interactWithServer(String message) throws IOException {
        // send message
        ClientSocketChannel.getSocketInstance().sendMessageToServer(message);

        // listen to response

        // parse response
        return false;
    }
}
