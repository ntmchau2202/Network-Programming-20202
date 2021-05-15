package client.controller;

import client.network.ClientSocketChannel;
import entity.Player.RankPlayer;
import helper.MessageParser;
import protocol.StatusCode;

public class RegisterFormController extends BaseController {
    private MessageParser msgParser = new MessageParser();
    private RankPlayer loggedPlayer;

    public boolean isRegisterSuccessfully(String username, String password) throws Exception {
        String result = ClientSocketChannel.getSocketInstance().register(username, password);
        // parsing stuff here
        if (result.length() != 0) {
            StatusCode stat = msgParser.getStatusCode(result);
            if (stat.compareTo(StatusCode.SUCCESS) == 0) {
                String sessionID = String.valueOf(msgParser.getInfoField(result,"session_id"));
                int elo = (int)msgParser.getInfoField(result, "elo");
                loggedPlayer = new RankPlayer(username, sessionID, elo);
                return true;
            }
        }
        return false;
    }

    public RankPlayer getLoggedPlayer() {
        return loggedPlayer;
    }
}
