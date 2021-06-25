package message.logout;

import message.ServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class LogoutServerMessage extends ServerMessage {
    private String username, sessionID;

    public LogoutServerMessage(LogoutClientMessage logoutClientMessage, StatusCode statCode, String errMsg) {
        super(statCode, errMsg, logoutClientMessage.getMessageCommandID());
        this.username = logoutClientMessage.getUsername();
        this.sessionID = logoutClientMessage.getSessionID();
        this.setCommand(Command.LOGOUT);
        this.responseBody.createLogoutBody(username, sessionID);
        this.finalizeMessageObject();
    }

    public LogoutServerMessage(int messageCommandID, String username, String sessionID, StatusCode statCode, String errMsg) {
        super(statCode, errMsg, messageCommandID);
        this.username = username;
        this.sessionID = sessionID;
        this.setCommand(Command.LOGOUT);
        this.responseBody.createLogoutBody(username, sessionID);
        this.finalizeMessageObject();
    }

    public LogoutServerMessage(String input) {
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
