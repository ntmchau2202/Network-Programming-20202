package client.controller;

import java.util.ArrayList;

import client.network.ClientSocketChannel;
import entity.Player.LeaderboardPlayer;
import message.joinqueue.JoinQueueServerMessage;
import message.login.LoginServerMessage;
import message.quitqueue.QuitQueueServerMessage;
import protocol.Attachment;
import protocol.StatusCode;
import entity.Player.Player;
import entity.Player.RankPlayer;

import java.util.List;


public class GameModeScreenController extends BaseController {
    private RankPlayer curPlayer;
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
    public int findPracticeGame()  {

        // TODO: make an interactive pop up here for waiting for server response
        try {
		String result = ClientSocketChannel.getSocketInstance().joinQueue(curPlayer.getSessionId(), "normal");
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
        } catch (Exception e) {
        	e.printStackTrace();
        	return 1;
        }
       
    }
    
    // note: duplicate code, must refactor!!!
    public int findRankedGame() {

        // TODO: make an interactive pop up here for waiting for server response
        try {
		String result = ClientSocketChannel.getSocketInstance().joinQueue(curPlayer.getSessionId(), "ranked");
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
        } catch (Exception e) {
        	e.printStackTrace();
        	return 1;
        }
    }
    
    public boolean quitQueue() {
        try {
    		String result =  ClientSocketChannel.getSocketInstance().quitQueue(curPlayer.getUsername(), curPlayer.getSessionId());
    		QuitQueueServerMessage response = new QuitQueueServerMessage(result);
    	        if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
    	            return false;
    	        } else {
    	        	return true;
    	        }
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
    }

//    public boolean isLogoutSuccessfully(String username, String sessionID) throws Exception {
//        try {
//            String result = ClientSocketChannel.getSocketInstance().logout(username, sessionID);
//            System.out.println("Returned message from Logout: "+ result);
//           //LogoutServerMessage response = new LogoutServerMessage(result);
//            if (response.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
//                return false;
//            } else {
//                return true;
//            }
//        } catch (Exception e) {
//            System.out.println("================== Error from logout");
//            e.printStackTrace();
//            return false;
//        }
//    }

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
}
