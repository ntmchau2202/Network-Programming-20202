package client.controller;

import java.io.IOException;

import client.network.ClientSocketChannel;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import protocol.StatusCode;

public class MainGameScreenController extends BaseController {
	
	private int moveX, moveY;
	private String currentPlayer, movePlayer;
	private String moveResult, moveState;
	private boolean isMyTurn;
	
	public MainGameScreenController(String currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public void setTurn(boolean turn) {
		this.isMyTurn = turn;
	}
	
	public boolean isMyTurn() {
		return this.isMyTurn;
	}

	public String getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean sendMove(int x, int y) throws Exception {
		String result = ClientSocketChannel.getSocketInstance().move(currentPlayer, x, y, "valid", "");

		MoveServerMessage move = new MoveServerMessage(result);
		
		if(move.getStatusCode().compareTo(StatusCode.SUCCESS)==0) {
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
}
