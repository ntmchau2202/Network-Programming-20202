package message.quitgame;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class QuitGameServerMessage extends ServerMessage {
	 private String username, sessionID;

	    public QuitGameServerMessage(QuitGameClientMessage quitGameClientMessage, StatusCode statCode, String errMsg) {
	        super(statCode, errMsg, quitGameClientMessage.getMessageCommandID());
	        this.username = quitGameClientMessage.getUsername();
	        this.sessionID = quitGameClientMessage.getSessionID();
	        this.setCommand(Command.QUIT_GAME);
	        this.responseBody.createQuitGameBody(username, sessionID);
	        this.finalizeMessageObject();
	    }

	    public QuitGameServerMessage(int messageCommandID, String username, String sessionID, StatusCode statCode, String errMsg) {
	        super(statCode, errMsg, messageCommandID);
	        this.username = username;
	        this.sessionID = sessionID;
	        this.setCommand(Command.QUIT_GAME);
	        this.responseBody.createQuitGameBody(username, sessionID);
	        this.finalizeMessageObject();
	    }

	    public QuitGameServerMessage(String input) {
	        super(input);
	        this.username = this.responseBody.getBody().getString("username");
	        this.sessionID = this.responseBody.getBody().getString("session_id");
	    }

	    public String getUsername() {
	        return this.username;
	    }

	    public String getSessionID() {
	        return this.sessionID;
	    }

}
