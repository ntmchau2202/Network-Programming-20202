package client.controller;

import client.entity.Player;
import client.network.ClientSocketChannel;
import message.joinqueue.JoinQueueServerMessage;
import protocol.StatusCode;

public class GameModeScreenController extends BaseController{
    private Player curPlayer;
    public GameModeScreenController(Player curPlayer) {
        this.curPlayer = curPlayer;
    }

    public Player getCurPlayer() {
        return this.curPlayer;
    }

    public boolean findPracticeGame() throws Exception {
        String result = ClientSocketChannel.getSocketInstance().joinQueue("normal");
        JoinQueueServerMessage response = new JoinQueueServerMessage(result);
        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
        	return false;
        }
        return true;
//    	return true;
    }

    public boolean findRankGame() throws Exception {
        String result = ClientSocketChannel.getSocketInstance().joinQueue("ranked");
        JoinQueueServerMessage response = new JoinQueueServerMessage(result);
        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
        	return false;
        }
        return true;
//    	return true;
    }
}
