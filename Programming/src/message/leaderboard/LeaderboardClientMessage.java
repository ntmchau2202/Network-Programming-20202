package message.leaderboard;

import message.ClientMessage;

public class LeaderboardClientMessage extends ClientMessage {
	private String username, sessionID;
	public LeaderboardClientMessage(String username, String sessionID) {
		super();
		this.username = username;
		this.sessionID = sessionID;
		
		this.requestBody.createLeaderBoardBody();
		this.finalizeMessageObject();
	}
	
	public LeaderboardClientMessage(String input) {
		super(input);
		
		this.username = this.requestBody.getBody().getString("username");
		this.sessionID = this.requestBody.getBody().getString("session_id");
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}
}
