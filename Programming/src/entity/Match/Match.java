package entity.Match;

import java.util.ArrayList;
import java.util.Random;

import entity.Move.Move;
import entity.Player.Player;
import javafx.util.Pair;
import server.entity.chat.ChatMessage;

public class Match {
	private Player player1;
	private Player player2;
	private int matchID;
	private ArrayList<Move> moveRecord;
	private ArrayList<ChatMessage> chatMsgRecord;
	
	public Match(Player player1, Player player2) {
		// TODO: think about duplicated match ID here
		Random random = new Random();
		this.player1 = player1;
		this.player2 = player2;
		this.matchID = random.nextInt(9216);
		moveRecord = new ArrayList<Move>();
		chatMsgRecord = new ArrayList<ChatMessage>();
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
	
	public boolean isPlayerOfMatch(Player player) {
		
		return player.getSessionId().equalsIgnoreCase(player1.getSessionId()) || player.getSessionId().equalsIgnoreCase(player2.getSessionId());
	}
	
	public Move getLatestMove(){
		return this.moveRecord.get(moveRecord.size()-1);
	}
	
	public void addNewMoveRecord(int x, int y, String movePlayer, String state, String result) {
		Move newMove = new Move(x, y, movePlayer, state, result);
		this.moveRecord.add(newMove);
	}
	
	public int getNumberOfMoves() {
		return this.moveRecord.size();
	}

	public ChatMessage getUnreadMsg() {
		ChatMessage chatMsg = null;
		for (ChatMessage cm: chatMsgRecord) {
			if (!cm.isRead()) {
				chatMsg = cm;
				break;
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
}
