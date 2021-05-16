package entity.Player;

public abstract class Player {
    private final String username;
    private final String sessionId;

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

}
