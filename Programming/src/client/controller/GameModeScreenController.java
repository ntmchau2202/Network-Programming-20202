package client.controller;

import client.network.ClientSocketChannel;
import message.joinqueue.JoinQueueServerMessage;
import protocol.StatusCode;
import entity.Player.Player;
import entity.Player.RankPlayer;


public class GameModeScreenController extends BaseController{
    private RankPlayer curPlayer;
    private JoinQueueServerMessage response;
    private int matchID, opponentElo;
    private String opponentName;
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
        response = new JoinQueueServerMessage(result);
        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
        	return false;
        }
        matchID = response.getMatchID();
        opponentName = response.getOpponent();
        opponentElo = response.getOpponentELO();
        sessionID = response.getSessionID();
        return true;
    }
    
    public boolean amIFirstPlayer() {
    	return this.response.getFirstPlayer().equalsIgnoreCase(this.curPlayer.getUsername());
    }
    
    public String getOpponent() {
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
