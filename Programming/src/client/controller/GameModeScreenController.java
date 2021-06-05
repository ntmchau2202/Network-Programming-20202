package client.controller;

import client.network.ClientSocketChannel;
import message.joinqueue.JoinQueueServerMessage;
import message.quitqueue.QuitQueueServerMessage;
import protocol.StatusCode;
import entity.Player.Player;
import entity.Player.RankPlayer;


public class GameModeScreenController extends BaseController {
    private RankPlayer curPlayer;
//    private JoinQueueServerMessage response;
    private int matchID, opponentElo;
    private String opponentName;
    private String firstPlayer;
    private String sessionID;

    public GameModeScreenController(RankPlayer curPlayer) {
        this.curPlayer = curPlayer;
    }

    public RankPlayer getCurPlayer() {
        return this.curPlayer;
    }

    public boolean findPracticeGame() throws Exception {
        System.out.println("Waiting for a game...");

        // TODO: make an interactive pop up here for waiting for server response
        String result = ClientSocketChannel.getSocketInstance().joinQueue(curPlayer.getSessionId(), "normal");
        JoinQueueServerMessage response = new JoinQueueServerMessage(result);
        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
            return false;
        }
        matchID = response.getMatchID();
        opponentName = response.getOpponent();
        opponentElo = response.getOpponentELO();
        sessionID = response.getSessionID();
        firstPlayer = response.getFirstPlayer();
        return true;
    }
    
    public boolean quitQueue() throws Exception {
        String result = ClientSocketChannel.getSocketInstance().quitQueue(curPlayer.getUsername(), curPlayer.getSessionId());
        QuitQueueServerMessage response = new QuitQueueServerMessage(result);
        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
            return false;
        } else {
        	return true;
        }
    }

    public boolean amIFirstPlayer() {
        return this.firstPlayer.equalsIgnoreCase(this.curPlayer.getUsername());
    }

    public String getOpponentName() {
        return this.opponentName;
    }

    public int getOpponentElo() {
        return this.opponentElo;
    }

    public int getMatchID() {
        return this.matchID;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public boolean findRankGame() throws Exception {
//        String result = ClientSocketChannel.getSocketInstance().joinQueue("ranked");
//        JoinQueueServerMessage response = new JoinQueueServerMessage(result);
//        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
//        	return false;
//        }
        return true;
//    	return true;
    }
}
