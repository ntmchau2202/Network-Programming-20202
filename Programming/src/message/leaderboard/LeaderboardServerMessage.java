package message.leaderboard;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class LeaderboardServerMessage extends ServerMessage {
	private List<String> listUsername;
	private List<Integer> listElo, listRank, listNoMatchPlayed, listNoMatchWon;
	
	public LeaderboardServerMessage(int messageCommandID, List<String> listUsr, List<Integer> listElo, List<Integer> listRank, List<Integer> listNoMatchPlayed, List<Integer> listNoMatchWon, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, messageCommandID);
		this.listUsername = listUsr;
		this.listElo = listElo;
		this.listRank = listRank;
		this.listNoMatchPlayed = listNoMatchPlayed;
		this.listNoMatchWon = listNoMatchWon;
		
		JSONArray jsArrayUser = new JSONArray(listUsr);
		JSONArray jsArrayElo = new JSONArray(listElo);
		JSONArray jsArrayRank = new JSONArray(listRank);
		JSONArray jsArrayNoMatchPlayed = new JSONArray(listNoMatchPlayed);
		JSONArray jsArrayNoMatchWon = new JSONArray(listNoMatchWon);
		this.setCommand(Command.LEADERBOARD);
		this.responseBody.createLeaderBoardBody(jsArrayUser, jsArrayElo, jsArrayRank, jsArrayNoMatchPlayed, jsArrayNoMatchWon);
		this.finalizeMessageObject();
	}
	
	public LeaderboardServerMessage(String input) {
		super(input);
		
		this.listUsername = new ArrayList<String>();
		this.listElo = new ArrayList<Integer>();
		this.listRank = new ArrayList<Integer>();
		
		List<Object> tmp = this.responseBody.getBody().getJSONArray("username").toList();
		
		for (Object u : tmp) {
			this.listUsername.add((String)u);
		}
		
		tmp = this.responseBody.getBody().getJSONArray("elo").toList();
		
		for (Object e : tmp) {
			this.listElo.add((int)e);
		}
		
		tmp = this.responseBody.getBody().getJSONArray("rank").toList();
		
		for (Object r : tmp) {
			this.listRank.add((int)r);
		}

		tmp = this.responseBody.getBody().getJSONArray("no_match_played").toList();

		for (Object r : tmp) {
			this.listNoMatchPlayed.add((int)r);
		}

		tmp = this.responseBody.getBody().getJSONArray("no_match_won").toList();

		for (Object r : tmp) {
			this.listNoMatchWon.add((int)r);
		}
		
	}

	public List<String> getListUsers(){
		return this.listUsername;
	}

	public List<Integer> getListELO(){
		return this.listElo;
	}
	
	public List<Integer> getListRank(){
		return this.listRank;
	}
	
}
