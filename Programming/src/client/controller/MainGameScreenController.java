package client.controller;

import java.io.IOException;

import org.json.JSONObject;

import client.network.ClientSocketChannel;
import entity.Player.Player;
import javafx.application.Platform;
import message.chat.ChatServerMessage;
import message.chatack.ChatACKServerMessage;
import message.drawconfirm.DrawConfirmServerMessage;
import message.drawrequest.DrawRequestServerMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import protocol.Command;
import protocol.StatusCode;

public class MainGameScreenController extends BaseController {

	private int moveX, moveY;
	private Player currentPlayer;
	private String movePlayer, opponentPlayer;
	private String moveResult, moveState;
	private boolean isMyTurn;
	private int matchID, opponentElo;
	private boolean isFirstPlayer;

	public MainGameScreenController(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public void setIsFirstPlayer(boolean isFirstPlayer) {
		this.isFirstPlayer = isFirstPlayer;
	}

	public void setTurn(boolean turn) {
		this.isMyTurn = turn;
	}

	public void setOpponent(String opponent, int elo) {
		this.opponentPlayer = opponent;
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

	public boolean sendMove(int x, int y) throws Exception {
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
//	public void listening() {
//		try {
//			String unconditionalMessage = ClientSocketChannel.getSocketInstance().listenIngameMessage();
//			JSONObject jsMsg = new JSONObject(unconditionalMessage);
//			Command cmd = Command.toCommand(jsMsg.getString("command_code"));
//
//			switch (cmd) {
//				case MOVE: {
//					MoveServerMessage moveMsg = new MoveServerMessage(unconditionalMessage);
//					int x = moveMsg.getX();
//					int y = moveMsg.getY();
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//
//				}
//				case CHAT: {
//					ChatServerMessage chatMsg = new ChatServerMessage(unconditionalMessage);
//					String incommingMsg = chatMsg.getMessage();
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//
//					// TODO: do analysis here
//
//				}
//				case CHATACK: {
//					ChatACKServerMessage ack = new ChatACKServerMessage(unconditionalMessage);
//					String msgID = ack.getMessageID();
//					int matchID = ack.getMatchID();
//
//					// TODO: do analysis here
//
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//
//				case DRAW_REQUEST: {
//					DrawRequestServerMessage drawRequest = new DrawRequestServerMessage(unconditionalMessage);
//					int matchID = drawRequest.getMatchID();
//					// TODO: do analysis here
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//
//				}
//				case DRAW_CONFIRM: {
//					DrawConfirmServerMessage drawConfirm = new DrawConfirmServerMessage(unconditionalMessage);
//					int matchID = drawConfirm.getMatchID();
//					boolean acceptance = drawConfirm.getAcceptance();
//
//					// TODO: do analysis here
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//
//			}
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
