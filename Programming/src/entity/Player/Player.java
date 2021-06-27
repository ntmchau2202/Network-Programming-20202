package entity.Player;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class Player {
    private final String username;
    private final String sessionId;
    private int elo;
    private AsynchronousSocketChannel userSocket;

    public Player(String username, String sessionId, int elo) {
        this.username = username;
        this.sessionId = sessionId;
        this.elo = elo;
    }
    
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
    
    public int getElo() {
    	return this.elo;
    }

}
