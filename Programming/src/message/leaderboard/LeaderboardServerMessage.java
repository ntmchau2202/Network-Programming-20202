package message.leaderboard;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class LeaderboardServerMessage extends ServerMessage {
	private List<String> listUsername;
	private List<Integer> listElo, listRank;
	
	public LeaderboardServerMessage(int messageCommandID, List<String> listUsr, List<Integer> listElo, List<Integer> listRank, StatusCode statCode, String errMsg) {
		super(statCode, errMsg, messageCommandID);
		this.listUsername = listUsr;
		this.listElo = listElo;
		this.listRank = listRank;
		
		JSONArray jsArrayUser = new JSONArray(listUsr);
		JSONArray jsArrayElo = new JSONArray(listElo);
		JSONArray jsArrayRank = new JSONArray(listRank);
		this.setCommand(Command.LEADERBOARD);
		this.responseBody.createLeaderBoardBody(jsArrayUser, jsArrayElo, jsArrayRank);
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
