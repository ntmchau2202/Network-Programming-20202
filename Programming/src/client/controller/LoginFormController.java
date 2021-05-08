package client.controller;

import client.network.ClientSocketChannel;
import protocol.ClientMessage;

import java.io.IOException;

public class LoginFormController extends BaseController {
    public boolean isLoginSuccessfully(String username, String password) throws IOException {
        interactWithServer(getLoginMessage(username, password));
        return false;
    }

    public String getLoginMessage(String username, String password) {
        ClientMessage loginRequest = new ClientMessage();
        loginRequest.createLoginRequest(username, password);
        return loginRequest.toString();
    }

    public boolean interactWithServer(String message) throws IOException {
        // send message
        ClientSocketChannel.getSocketInstance().sendMessageToServer(message);

        // listen to response

        // parse response
        return false;
    }
}
