package server.entity.chat;

import entity.Player.Player;

public class ChatMessage {
    private String sendPlayerName;
    private String recvPlayerName;
    private String message;
    private String messageID;
    private int matchID;
    private boolean isRead = false;

    public ChatMessage(int matchID, String sendPlayerName, String recvPlayerName, String messageID, String message) {
        this.matchID = matchID;
        this.sendPlayerName = sendPlayerName;
        this.recvPlayerName = recvPlayerName;
        this.messageID = messageID;
        this.message = message;
    }

    public int getMatchID() {
        return matchID;
    }

    public String getSendPlayerName() {
        return sendPlayerName;
    }

    public String getRecvPlayerName() {
        return recvPlayerName;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageID() {
        return messageID;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
