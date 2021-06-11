package client.controller;

import client.network.ClientSocketChannel;
import message.joinqueue.JoinQueueServerMessage;
import message.quitqueue.QuitQueueServerMessage;
import protocol.Attachment;
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
    // return code: 0 == success, 1 == unexpected error, -1 == user quit queue
    public int findPracticeGame() throws Exception {
        System.out.println("Waiting for a game...");

        // TODO: make an interactive pop up here for waiting for server response
        Attachment resultAttachment = ClientSocketChannel.getSocketInstance().joinQueue(curPlayer.getSessionId(), "normal");
        while(true) {
        	if(!resultAttachment.getActive().get()) {
        		String result = resultAttachment.getReturnMessage();
        		 JoinQueueServerMessage response = new JoinQueueServerMessage(result);
        	        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
        	            if(response.getErrorMessage().contains("QUIT_QUEUE")) {
        	            	return -1;
        	            } else {
        	            	return 1;
        	            }
        	        }
        	        matchID = response.getMatchID();
        	        opponentName = response.getOpponent();
        	        opponentElo = response.getOpponentELO();
        	        sessionID = response.getSessionID();
        	        firstPlayer = response.getFirstPlayer();
        	        return 0;
        	}
        }
       
    }
    
    public boolean quitQueue() throws Exception {
        Attachment resultAttachment = ClientSocketChannel.getSocketInstance().quitQueue(curPlayer.getUsername(), curPlayer.getSessionId());
        while(true) {
        	if(!resultAttachment.getActive().get()) {
        		String result = resultAttachment.getReturnMessage();
        		 QuitQueueServerMessage response = new QuitQueueServerMessage(result);
        	        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
        	            return false;
        	        } else {
        	        	return true;
        	        }
        	}
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
