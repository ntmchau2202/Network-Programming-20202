package client.controller;

import client.network.ClientSocketChannel;
import entity.Player.GuestPlayer;
import entity.Player.Player;
import entity.Player.RankPlayer;
import message.joinqueue.JoinQueueServerMessage;
import protocol.StatusCode;

public class HomeScreenController extends BaseController{
    private GuestPlayer curPlayer;
    private int matchID, opponentElo;
    private String opponentName;
    private String firstPlayer;
    private String sessionID;
    
	public HomeScreenController() {
		
	}
	
	public int findGuestGame() {
		 System.out.println("Waiting for a game...");

	        // TODO: make an interactive pop up here for waiting for server response
	        try {
			String result = ClientSocketChannel.getSocketInstance().joinQueue("", "normal");
			System.out.println("Returned message to findGuestGame: " + result);
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
		        curPlayer = new GuestPlayer(response.getUsername(), response.getSessionID());
//		        curPlayer.setUserSocket(ClientSocketChannel.getSocketInstance());
		        return 0;
	        } catch (Exception e) {
	        	System.out.println("============= Error from join queue");
	        	e.printStackTrace();
	        	return 1;
	        }
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
    
    public boolean amIFirstPlayer() {
        return this.firstPlayer.equalsIgnoreCase(this.curPlayer.getUsername());
    }
    
    public Player getCurrentPlayer() {
    	return this.curPlayer;
    }
}
