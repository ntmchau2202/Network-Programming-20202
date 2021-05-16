package message.endgame;

import org.json.JSONObject;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class EndgameServerMessage extends ServerMessage {
	private int matchID, winnerELO, loserELO;
	private String winner, loser;
	private boolean isDraw;

	public EndgameServerMessage(int matchID, String winner, int winnerElo, int loserElo, String loser, boolean isDraw, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		
		this.matchID = matchID;
		this.winner = winner;
		this.loser = loser;
		this.winnerELO = winnerElo;
		this.loserELO = loserElo;
		this.isDraw = isDraw;
		
		this.setCommand(Command.ENDGAME);
		this.responseBody.createEndgameBody(matchID, winner, winnerElo, loser, loserElo, isDraw);
		this.finalizeMessageObject();
		
	}
	
	public EndgameServerMessage(String input) {
		super(input);
		
		this.matchID = this.responseBody.getBody().getInt("match_id");
		JSONObject winnerInf = this.responseBody.getBody().getJSONObject("winner");
		JSONObject loserInf = this.responseBody.getBody().getJSONObject("loser");
		this.winner = winnerInf.getString("username");
		this.winnerELO = winnerInf.getInt("elo");
		this.loser = loserInf.getString("username");
		this.loserELO = loserInf.getInt("elo");
		this.isDraw = this.responseBody.getBody().getBoolean("isDraw");
	}
	
	public int getMatchID() {
		return this.matchID;
	}
	
	public boolean isDraw() {
		return this.isDraw;
	}
	
	public String getWinner() {
		return this.winner;
	}
	
	public int getWinnerELO() {
		return this.winnerELO;
	}
	
	public String getLoser() {
		return this.loser;
	}
	
	public int getLoserELO() {
		return this.loserELO;
	}
	
}
