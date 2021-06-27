package client.controller;

import entity.Player.LeaderboardPlayer;
import entity.Player.LeaderboardPlayerList;
import message.leaderboard.LeaderboardServerMessage;
import protocol.StatusCode;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import client.network.ClientSocketChannel;

public class LeaderBoardController extends BaseController{
	private String username, sessionID, errMsg;
	private ArrayList<LeaderboardPlayer> listLeaderboard;
	public LeaderBoardController(String username, String sessionID) {
		this.username = username;
		this.sessionID = sessionID;
		this.listLeaderboard = new ArrayList<LeaderboardPlayer>();
	}
	
    public boolean getLeaderboard() throws Exception {
//        List<LeaderboardPlayer> lstPlayer = new ArrayList<>();
//
//        lstPlayer = (List<LeaderboardPlayer>) LeaderboardPlayerList.getLeaderboardPlayerListInstance().getLeaderboardPlayerList();
//        return lstPlayer;
    	String result = ClientSocketChannel.getSocketInstance().getLeaderBoard(this.username, this.sessionID);
    	LeaderboardServerMessage msg = new LeaderboardServerMessage(result);
    	if(msg.getStatusCode().compareTo(StatusCode.ERROR)==0) {
    		this.errMsg = msg.getErrorMessage();
    		return false;
    	} else {
    		// reject if size of 3 arrays are not the same
    		int size1 = msg.getListUsers().size();
    		int size2 = msg.getListRank().size();
    		int size3 = msg.getListMatchWon().size();
    		int size4 = msg.getListMatchPlayed().size();
    		int size5 = msg.getListELO().size();
    		System.out.println("Get all the sizes");
    		if (size1 != size2 || size1 != size3 || size1 != size4 || size1 != size5) {
    			System.out.println("Sizes are different");
    			return false;
    		} else {
    			// parsing
    			this.listLeaderboard.clear();
    			System.out.println("Start parsing data");
    			for(int i = 0; i < msg.getListUsers().size(); i++) {
    				//(int rank, String username, int noPlayedMatch, int noWonMatch, int elo)
    				LeaderboardPlayer lp = new LeaderboardPlayer(msg.getListRank().get(i), msg.getListUsers().get(i), msg.getListMatchPlayed().get(i), msg.getListMatchWon().get(i), msg.getListELO().get(i));
    				this.listLeaderboard.add(lp);
    			}
    			return true;
    		}
    	}
    	
    }
    
    public ArrayList<LeaderboardPlayer> getLeaderboardList(){
    	return this.listLeaderboard;
    }
}
