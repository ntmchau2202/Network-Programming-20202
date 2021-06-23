package message.drawrequest;

import message.ClientMessage;
import protocol.Command;

public class ListenDrawClientMessage extends ClientMessage  {
		private String username;
		private int matchID;
		
		public ListenDrawClientMessage(String username, int matchID) {
			super();
			this.username = username;
			this.matchID = matchID;
			
			this.setCommand(Command.LISTEN_DRAW);
			this.requestBody.createListenDrawRequestBody(username, matchID);
			this.finalizeMessageObject();
			
		}
		
		public ListenDrawClientMessage(String inputMessage) {
			super(inputMessage);
			
			this.username = this.requestBody.getBody().getString("username");
			this.matchID = this.requestBody.getBody().getInt("match_id");
		}
		
		public String getUsername() {
			return this.username;
		}
		
		public int getMatchID() {
			return this.matchID;
		}
}
