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
		// this.responseBody.createMatchFoundBody();
		this.finalizeMessageObject();
	}
	
	public MatchFoundServerMessage(String input) {
		super(input);
		
		this.matchID = (int)this.responseBody.getKey("match_id");
		this.elo = (int)this.responseBody.getKey("elo");
		this.opponent = (String)this.responseBody.getKey("opponent");
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
