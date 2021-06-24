package client.controller;

import client.network.ClientSocketChannel;
import entity.Player.Player;
import entity.Player.RankPlayer;
import message.ServerMessage;
import message.chat.ChatServerMessage;
import message.drawconfirm.DrawConfirmServerMessage;
import message.drawrequest.DrawRequestServerMessage;
import message.move.MoveServerMessage;
import message.updateuser.UpdateUserServerMessage;
import protocol.Command;
import protocol.StatusCode;
import server.entity.match.ChatMessage;
import org.json.JSONObject;

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
    private String errMsg;
    private String mode;

    public MainGameScreenController(Player currentPlayer, String mode) {
        this.currentPlayer = currentPlayer;
        this.mode = mode;
    }

    public String getCurrentGameMode() {
        return this.mode;
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

    public int getMatchID() {
        return this.matchID;
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

    public String getOpponentPlayerName() {
        return opponentPlayerName;
    }

    public int getOpponentElo() {
        return opponentElo;
    }

    public boolean sendMove(int x, int y, String gameResult) throws Exception {
        System.out.println("Match id: " + this.matchID);
        System.out.println("=====================Sending move: " + x + " - " + y);
        String result = ClientSocketChannel.getSocketInstance().move(currentPlayer.getUsername(),
                currentPlayer.getSessionId(), this.matchID, x, y, "valid", gameResult);
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

    public ChatMessage listenChat() throws Exception {
        System.out.println("=====================listening chat");
        String result = ClientSocketChannel.getSocketInstance().listenChat(currentPlayer.getUsername(), this.matchID);
        ChatServerMessage chat = new ChatServerMessage(result);

        if (chat.getStatusCode().compareTo(StatusCode.SUCCESS) == 0) {
            //(int matchID, String sendPlayerName, String recvPlayerName, String messageID, String message)
            return new ChatMessage(chat.getMatchID(), chat.getSendUser(), chat.getReceiveUser(), chat.getChatMessageID(), chat.getChatMessage());
        } else {
            return null;
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

    public String getFinalMovePlayer() {
        return this.movePlayer;
    }

    public ChatMessage sendChatMessage(String chatMsg) throws Exception {
        //(String fromUsr, String toUsr, String msg, int matchID)
        System.out.println("Gotta send msg: " + chatMsg);
        String result = ClientSocketChannel.getSocketInstance().chat(currentPlayer.getUsername(), opponentPlayerName, chatMsg, matchID);
        ChatServerMessage ret = new ChatServerMessage(result);
        if (ret.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
            this.errMsg = ret.getErrorMessage();
            return null;
        } else {
            return new ChatMessage(ret.getMatchID(), ret.getSendUser(), ret.getReceiveUser(), ret.getChatMessageID(), ret.getChatMessage());
        }
    }

    public String getFinalErrorMessage() {
        return this.errMsg;
    }

    public boolean updateUserInformation() throws Exception {
        System.out.println("We gonna update ourselves here, not like the dumbass Windows :)");
        String result = ClientSocketChannel.getSocketInstance().updateUser(currentPlayer.getUsername(), currentPlayer.getSessionId());
        UpdateUserServerMessage ret = new UpdateUserServerMessage(result);
        if (ret.getStatusCode().compareTo(StatusCode.ERROR) == 0) {
            return false;
        } else {
            // this only be called when it's ranked player, so cast
            // however, still considering this is unsafe
            // how to resolve this?
            // -> check if player is instance of RankPlayer
            if (currentPlayer instanceof RankPlayer) {
                // downcast
                RankPlayer tmp = (RankPlayer) currentPlayer;
                tmp.updatePlayerInfo(ret.getRank(), ret.getElo(), ret.getNoMatchPlayed(), ret.getNoMatchWon());
                // upcast
                currentPlayer = tmp;
                return true;
            } else {
                return false;
            }
        }
    }
    
    public ServerMessage listenDrawRequest() throws Exception {
    	String result = ClientSocketChannel.getSocketInstance().listenDrawRequest(this.getCurrentPlayer().getUsername(), this.matchID);
    	// parsing to get the command
    	JSONObject jsRes = new JSONObject(result);
    	Command cmd = Command.toCommand(jsRes.getString("command_code"));
    	switch(cmd) {
    	case DRAW_REQUEST:{
    		DrawRequestServerMessage ret = new DrawRequestServerMessage(result);
    		// only return if the response does not come from current user to avoid duplication
    		if (ret.getStatusCode().compareTo(StatusCode.ERROR) != 0 && ret.getPlayer().compareToIgnoreCase(this.getCurrentPlayer().getUsername())==0) {
    			return ret;	
    		} else {
    			return null;
    		}
    	}
    	case DRAW_CONFIRM:{
    		DrawConfirmServerMessage ret = new DrawConfirmServerMessage(result);
    		// only return if the response does not come from current user to avoid duplication
    		if (ret.getStatusCode().compareTo(StatusCode.ERROR) != 0 && ret.getPlayer().compareToIgnoreCase(this.getCurrentPlayer().getUsername())==0) {
    			return ret;	
    		} else {
    			return null;
    		}
    	}
    	default:
    		return null;
    	}
    	
    }

}
