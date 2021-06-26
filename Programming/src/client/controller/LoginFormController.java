package client.controller;

import client.network.ClientSocketChannel;
import entity.Player.LeaderboardPlayer;
import entity.Player.RankPlayer;
import message.login.LoginServerMessage;
import protocol.StatusCode;

import java.util.List;

public class LoginFormController extends BaseController {
	private RankPlayer loggedPlayer;
    public boolean isLoginSuccessfully(String username, String password) throws Exception {
        String result = ClientSocketChannel.getSocketInstance().login(username, password);
        LoginServerMessage serverResponse = new LoginServerMessage(result);
        if (serverResponse.getStatusCode().compareTo(StatusCode.SUCCESS) == 0) {
        	String sessionID = serverResponse.getSessionID();
        	String returnedUsername = serverResponse.getUsername();
//        	LeaderBoardController leaderBoardController = new LeaderBoardController();
//        	int rank = 0;
//            List<LeaderboardPlayer> players = leaderBoardController.getTopPlayers();
//            for (LeaderboardPlayer player : players) {
//                if (player.getUsername().equals(serverResponse.getUsername())) {
//                    rank = player.getRank();
//                }
//            }
//            System.out.println("The rank is: " + rank);
        	loggedPlayer = new RankPlayer(returnedUsername, sessionID, serverResponse.getRank() ,serverResponse.getELO(),serverResponse.getNumberOfMatchPlayed(),serverResponse.getNumberOfMatchWon());
        	return true;
        }
        
        return false;	
    }

    public RankPlayer getLoggedPlayer() {
        return loggedPlayer;
    }
}
