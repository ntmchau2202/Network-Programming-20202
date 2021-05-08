package protocol;

public enum Command {
	LOGIN("LOGIN"), 
	REGISTER("REGISTER"),
	JOIN_QUEUE("JOIN_QUEUE"),
	MATCH_FOUND("MATCH_FOUND"),
	MOVE("MOVE"),
	DRAW_REQUEST("DRAW_REQUEST"),
	DRAW_CONFIRM("DRAW_CONFIRM"),
	ENDGAME("ENDGAME"),
	LEADERBOARD("LEADERBOARD"),
	CHAT("CHAT"),
	CHATACK("CHATACK"),
	LOGOUT("LOGOUT");

    private String command;

    Command(String cmd) {
        this.command = cmd;
    }

    public String getCommandString() {
        return this.command;
    }
    
    public static Command toCommand(String inputString) {
    	for (Command c : Command.values()) {
    		if (c.getCommandString().compareToIgnoreCase(inputString) == 0) {
    			return c;
    		}
    	}
    	return null;
    }
}
