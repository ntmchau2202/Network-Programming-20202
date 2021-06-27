package test;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import entity.Player.RankPlayer;
import message.leaderboard.LeaderboardClientMessage;
import message.leaderboard.LeaderboardServerMessage;
import protocol.StatusCode;

public class TestProtocol {

	public static void main(String[] args) {

		// Test leaderboard
		LeaderboardClientMessage leaderboardRequest = new LeaderboardClientMessage("nAULRJBG", "hikaru");
		System.out.println(leaderboardRequest.toString());
		LeaderboardClientMessage leaderboardRequest2 =  new LeaderboardClientMessage(leaderboardRequest.toString());
		System.out.println(leaderboardRequest2.toString());

		ArrayList<String> listUsr = new ArrayList<String>();
		listUsr.add("duong");
		listUsr.add("chau");
		listUsr.add("haha");
		
		ArrayList<Integer> listElo = new ArrayList<Integer>();
		listElo.add(1900);
		listElo.add(1500);
		listElo.add(1400);
		
		ArrayList<Integer> listRank = new ArrayList<Integer>();
		listRank.add(2);
		listRank.add(9);
		listRank.add(13);
		
		ArrayList<Integer> listMatchPlayed = new ArrayList<Integer>();
		listMatchPlayed.add(17);
		listMatchPlayed.add(13);
		listMatchPlayed.add(12);
		
		
		ArrayList<Integer> listMatchWon = new ArrayList<Integer>();
		listMatchWon.add(16);
		listMatchWon.add(9);
		listMatchWon.add(7);
		
		//LeaderboardServerMessage(int messageCommandID, List<String> listUsr, List<Integer> listElo, List<Integer> listRank, List<Integer> listNoMatchPlayed, List<Integer> listNoMatchWon, StatusCode statCode, String errMsg)
		LeaderboardServerMessage leaderboardResponse = new LeaderboardServerMessage(leaderboardRequest2.getMessageCommandID(), listUsr, listElo, listRank, listMatchPlayed, listMatchWon, StatusCode.SUCCESS, "");
		System.out.println(leaderboardResponse.toString());
		LeaderboardServerMessage leaderboardResponse2 = new LeaderboardServerMessage(leaderboardResponse.toString());
		System.out.println(leaderboardResponse2.toString());
		
		ArrayList<RankPlayer> listPlayer = new ArrayList<RankPlayer>();
		for(int i = 0; i < listUsr.size(); i++) {
			// merge
			// mock player
			RankPlayer rp = new RankPlayer(listUsr.get(i), "", listElo.get(i));
			rp.updatePlayerInfo(listRank.get(i), listElo.get(i), listMatchPlayed.get(i), listMatchWon.get(i));
			listPlayer.add(rp);
			
		}
		System.out.println(listPlayer);
	}

}
