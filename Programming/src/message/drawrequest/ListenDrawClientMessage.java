package message.drawrequest;

import message.ClientMessage;
import protocol.Command;

public class ListenDrawClientMessage extends ClientMessage  {
	private String username, sessionID;
	private int matchID;

	public ListenDrawClientMessage(String username, String sessionID, int matchID) {
		super();
		this.username = username;
		this.sessionID = sessionID;
		this.matchID = matchID;

		this.setCommand(Command.LISTEN_DRAW);
		this.requestBody.createListenDrawRequestBody(username, sessionID, matchID);
		this.finalizeMessageObject();

	}

	public ListenDrawClientMessage(String inputMessage) {
		super(inputMessage);

		this.username = this.requestBody.getBody().getString("username");
		this.sessionID = this.requestBody.getBody().getString("session_id");
		this.matchID = this.requestBody.getBody().getInt("match_id");
	}

	public String getUsername() {
		return this.username;
	}

	public String getSessionID() {
		return sessionID;
	}

	public int getMatchID() {
		return this.matchID;
	}
}
