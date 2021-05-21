package message.matchfound;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class MatchFoundServerMessage extends ServerMessage {
	
	private int matchID, elo;
	private String opponent; 
	
	public MatchFoundServerMessage(int matchID, String opponent, int elo, StatusCode statCode, String errMsg) {
		super(statCode, errMsg);
		
		this.matchID = matchID;
		this.elo = elo;
		this.opponent = opponent;
		
		this.setCommand(Command.MATCH_FOUND);
		// this.responseBody.createMatchFoundBody(matchID, opponent, elo);
		this.finalizeMessageObject();
	}
	
	public MatchFoundServerMessage(String input) {
		super(input);
		
		this.matchID = this.responseBody.getBody().getInt("match_id");
		this.elo = this.responseBody.getBody().getInt("elo");
		this.opponent = this.responseBody.getBody().getString("opponent");
	}
	
	public int getMatchID() {
		return this.matchID;
	}
	
	public String getOpponentName() {
		return this.opponent;
	}
	
	public int getOpponentELO() {
		return this.elo;
	}
}
