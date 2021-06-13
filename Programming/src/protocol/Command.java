package protocol;

public enum Command {
	

	LOGIN("LOGIN", Command.Priority.NORMAL),
	REGISTER("REGISTER", Command.Priority.NORMAL),
	JOIN_QUEUE("JOIN_QUEUE", Command.Priority.NORMAL),
	MATCH_FOUND("MATCH_FOUND", Command.Priority.NORMAL),
	MOVE("MOVE", Command.Priority.NORMAL),
	DRAW_REQUEST("DRAW_REQUEST", Command.Priority.NORMAL),
	DRAW_CONFIRM("DRAW_CONFIRM", Command.Priority.HIGH),
	ENDGAME("ENDGAME", Command.Priority.NORMAL),
	LEADERBOARD("LEADERBOARD", Command.Priority.NORMAL),
	CHAT("CHAT", Command.Priority.NORMAL),
	LISTEN_CHAT("LISTEN_CHAT", Command.Priority.NORMAL),
	CHATACK("CHATACK", Command.Priority.NORMAL),
	LOGOUT("LOGOUT", Command.Priority.HIGH),
	LISTEN_MOVE("LISTEN_MOVE", Command.Priority.NORMAL),
	QUIT_QUEUE("QUIT_QUEUE", Command.Priority.CRITICAL);
	
	public enum Priority {
		CRITICAL(0),
		HIGH(1),
		NORMAL(2),
		LOW(3);
		
		private int priority;
		Priority(int priority){
			this.priority = priority;
		}
		
		public int getPriorityOrder() {
			return this.priority;
		}
	}
	
	

    private String command;
    private Priority priority;

    Command(String cmd, Priority priority) {
        this.command = cmd;
        this.priority = priority;
    }

    public String getCommandString() {
        return this.command;
    }
    
    public Priority getCommandPriority() {
    	return this.priority;
    }
    
    public int comparePriority(Command anotherCommand) {
    	if(this.priority.getPriorityOrder() > anotherCommand.getCommandPriority().getPriorityOrder()) {
    		return -1;
    	} else if (this.priority.getPriorityOrder() == anotherCommand.getCommandPriority().getPriorityOrder()) {
    		return 0;
    	} 
    	return 1;
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
