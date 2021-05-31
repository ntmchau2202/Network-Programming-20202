package client.controller;

import java.io.IOException;

import org.json.JSONObject;

import client.network.ClientSocketChannel;
import client.views.screen.MainGameScreenHandler;
import entity.Player.Player;
import javafx.application.Platform;
import message.ServerMessage;
import message.chat.ChatServerMessage;
import message.chatack.ChatACKServerMessage;
import message.drawconfirm.DrawConfirmServerMessage;
import message.drawrequest.DrawRequestServerMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class MainGameScreenController extends BaseController {

	// current player info
	private int moveX, moveY;
	private Player currentPlayer;
	private boolean isFirstPlayer;
	private boolean isMyTurn;

	// match info
	private int matchID;
	private String firstPlayerName;

	// opponent info
	private String opponentPlayerName;
	private int opponentElo;

	private String movePlayer;
	private String moveResult, moveState;

	public MainGameScreenController(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public void setIsFirstPlayer(boolean isFirstPlayer) {
		this.isFirstPlayer = isFirstPlayer;
		if (isFirstPlayer) {
			firstPlayerName = currentPlayer.getUsername();
		} else {
			firstPlayerName = opponentPlayerName;
		}
	}

	public void setTurn(boolean turn) {
		this.isMyTurn = turn;
	}

	public void setOpponent(String opponent, int elo) {
		this.opponentPlayerName = opponent;
		this.opponentElo = elo;
	}

	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}

	public boolean isMyTurn() {
		return this.isMyTurn;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean amIFirstPlayer() {
		return isFirstPlayer;
	}

	public boolean checkFirstPlayerByName(String playerName) {
		return playerName.equals(firstPlayerName);
	}

	public boolean sendMove(int x, int y) throws Exception {
		System.out.println("Match id: " + this.matchID);
		System.out.println("=====================Sending move: " + x + " - " + y);
		String result = ClientSocketChannel.getSocketInstance().move(currentPlayer.getUsername(),
				currentPlayer.getSessionId(), this.matchID, x, y, "valid", "");
		MoveServerMessage move = new MoveServerMessage(result);

		if (move.getStatusCode().compareTo(StatusCode.SUCCESS) == 0) {
			moveX = move.getX();
			moveY = move.getY();
			movePlayer = move.getMovePlayer();
			moveResult = move.getResult();
			moveState = move.getState();
			return true;
		} else {
			return false;
		}

	}

	public boolean listenMove() throws Exception {
		System.out.println("=====================listening move");
		String result = ClientSocketChannel.getSocketInstance().listenMove(currentPlayer.getUsername(), this.matchID);
		MoveServerMessage move = new MoveServerMessage(result);

		if (move.getStatusCode().compareTo(StatusCode.SUCCESS) == 0) {
			moveX = move.getX();
			moveY = move.getY();
			movePlayer = move.getMovePlayer();
			moveResult = move.getResult();
			moveState = move.getState();
			return true;
		} else {
			return false;
		}
	}

	public boolean isFinal() {
		return this.moveResult.compareToIgnoreCase("win") == 0;
	}

	public int getX() {
		return this.moveX;
	}

	public int getY() {
		return this.moveY;
	}

	// may change the return type here, to send information to GUI for modification
	public ServerMessage listening(MainGameScreenHandler screenHandler) {
		// screen handler is for displaying stuff on the screen
		ServerMessage svMsg = null;
		try {
			svMsg = ClientSocketChannel.getSocketInstance().listenIngameMessage();
			// analysis here
			if (svMsg instanceof MoveServerMessage) {
				MoveServerMessage moveMsg = (MoveServerMessage)svMsg;
				int x = moveMsg.getX();
				int y = moveMsg.getY();
				System.out.println("Recv (X,Y): "+ x + ":" +y);				
			} else if (svMsg instanceof ChatServerMessage) {
				
			} else if (svMsg instanceof ChatACKServerMessage) {
				
			} else if (svMsg instanceof DrawRequestServerMessage) {
				
			} else if (svMsg instanceof DrawConfirmServerMessage){
				// do st here
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return svMsg;
	}
}
