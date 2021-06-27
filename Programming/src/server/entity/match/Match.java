package server.entity.match;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import entity.Move.Move;
import entity.Player.Player;

public class Match {
	// match basic info
	private final Player player1;
	private final Player player2;
	private final int matchID;
	private final boolean isRanked;

	// match state
	// if match ends but winner is empty -> draw
	private String winner = ""; // sorry I dont know how to best place a winner as a Player in this case ; _ ;
	private AtomicBoolean isEnded;

	// move record
	private final ArrayList<Move> moveRecord;

	// chat msg record
	private final ArrayList<ChatMessage> chatMsgRecord;

	// draw request & state: for easy and short implementation: we use a state field for each player
	private String player1DrawRequestState = "";
	private String player2DrawRequestState = "";

	
	public Match(Player player1, Player player2, boolean isRanked) {
		// TODO: think about duplicated match ID here
		Random random = new Random();
		this.player1 = player1;
		this.player2 = player2;
		this.matchID = random.nextInt(9216);
		this.isRanked = isRanked;
		moveRecord = new ArrayList<>();
		chatMsgRecord = new ArrayList<>();
		winner = "";
		isEnded = new AtomicBoolean(false);
	}
	
	public Player getPlayer1() {
		return this.player1;
	}
	
	public Player getPlayer2() {
		return this.player2;
	}
	
	public int getMatchID() {
		return this.matchID;
	}
	
	public String getWinner() {
		return this.winner;
	}
	
	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	public void setEnd(boolean state) {
		this.isEnded.set(state);
	}
	
	public boolean isEnded() {
		return this.isEnded.get();
	}

	public boolean isRanked() {
		return isRanked;
	}

	public Player getPlayerByName(String username) {
		if (player1.getUsername().equals(username)) return player1;
		else if (player2.getUsername().equals(username)) return player2;
		return null;
	}

	public Player getAnotherPlayer(String username) {
		if (player1.getUsername().equals(username)) return player2;
		else if (player2.getUsername().equals(username)) return player1;
		return null;
	}

	
	public boolean isPlayerOfMatch(Player player) {
		return player.getSessionId().equalsIgnoreCase(player1.getSessionId()) || player.getSessionId().equalsIgnoreCase(player2.getSessionId());
	}
	
	public Move getLatestMove(){
		if (moveRecord.size() <= 0) return null;
		return this.moveRecord.get(moveRecord.size()-1);
	}
	
	public void addNewMoveRecord(int x, int y, String movePlayer, String state, String result) {
		Move newMove = new Move(x, y, movePlayer, state, result);
		this.moveRecord.add(newMove);
		System.out.println(this.moveRecord);
	}
	
	public int getNumberOfMoves() {
		return this.moveRecord.size();
	}

	public ChatMessage getUnreadMsg(String readMsgPlayerName) {
		ChatMessage chatMsg = null;
		// check if read msg player is in the match
		if (readMsgPlayerName.equals(player1.getUsername()) || readMsgPlayerName.equals(player2.getUsername())) {
			for (ChatMessage cm: chatMsgRecord) {
				if (readMsgPlayerName.equals(cm.getRecvPlayerName()) && !cm.isRead()) {
					chatMsg = cm;
					break;
				}
			}
		}
		// mark msg as read
		if (chatMsg != null) {
			chatMsg.setRead(true);
		}
		return chatMsg;
	}

	public boolean addNewMsgRecord(int matchID, String sendPlayer, String recvPlayer, String messageID, String message) {
		// check if send player / receive player exists in current match
		if ((!sendPlayer.equals(player1.getUsername()) && !sendPlayer.equals(player2.getUsername())) || (!recvPlayer.equals(player1.getUsername()) && !recvPlayer.equals(player2.getUsername()))) {
			return false;
		}
		ChatMessage chatMsg = new ChatMessage(matchID, sendPlayer, recvPlayer, messageID, message);
		this.chatMsgRecord.add(chatMsg);
		return true;
	}

	public boolean initDrawRequest(String drawRequestName) {
		if (player1.getUsername().equals(drawRequestName)) {
			this.player1DrawRequestState = "confirm";
			return true;
		} else if (player2.getUsername().equals(drawRequestName)) {
			this.player2DrawRequestState = "confirm";
			return true;
		}
		return false;
	}
	
	public void resetDrawRequest() {
		this.player1DrawRequestState = "";
		this.player2DrawRequestState = "";
	}

	public boolean confirmDrawRequest(String drawResponseName) {
		if (player1.getUsername().equals(drawResponseName)) {
			this.player1DrawRequestState = "confirm";
			return true;
		} else if (player2.getUsername().equals(drawResponseName)) {
			this.player2DrawRequestState = "confirm";
			return true;
		}
		return false;
	}

	public boolean denyDrawRequest(String drawResponseName) {
		if (player1.getUsername().equals(drawResponseName)) {
			this.player1DrawRequestState = "deny";
			return true;
		} else if (player2.getUsername().equals(drawResponseName)) {
			this.player2DrawRequestState = "deny";
			return true;
		}
		return false;
	}

	public boolean isIncomingDrawRequest(String listenName) {
		if (player1.getUsername().equals(listenName)) {
			return this.player2DrawRequestState.equals("confirm") && this.player1DrawRequestState.isEmpty();
		} else if (player2.getUsername().equals(listenName)) {
			return this.player1DrawRequestState.equals("confirm") && this.player2DrawRequestState.isEmpty();
		}
		return false;
	}

	public boolean isDrawSucceed() {
		return this.player1DrawRequestState.equals("confirm") && this.player2DrawRequestState.equals("confirm");
	}

	public boolean isDrawDeny() {
		return this.player1DrawRequestState.equals("deny") || this.player2DrawRequestState.equals("deny");
	}
}
