package entity.Player;

import java.nio.channels.AsynchronousSocketChannel;

public class Player {
    private final String username;
    private final String sessionId;
    private AsynchronousSocketChannel userSocket;

    public Player(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }
    
    public void setUserSocket(AsynchronousSocketChannel sock) {
    	this.userSocket = sock;	
    }
    
    public AsynchronousSocketChannel getUserSocket() {
    	return this.userSocket;
    }

}
