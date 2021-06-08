package server.network.completionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import entity.Match.Match;
import entity.Move.Move;
import entity.Player.Player;
import entity.Player.RankPlayer;
import message.joinqueue.JoinQueueClientMessage;
import message.joinqueue.JoinQueueServerMessage;
import message.login.LoginClientMessage;
import message.login.LoginServerMessage;
import message.move.ListenMoveClientMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import message.quitqueue.QuitQueueClientMessage;
import message.quitqueue.QuitQueueServerMessage;
import message.register.RegisterClientMessage;
import message.register.RegisterServerMessage;
import protocol.Attachment;
import protocol.Command;
import protocol.StatusCode;
import server.authentication.T3Authenticator;
import server.process.HandlerController;
import server.process.QueueController;


public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{
	private final AsynchronousSocketChannel socketChannel;
	private final ByteBuffer buffer;
	private String recvMsg;
	private QueueController queueController;
	private Command cmd;
	private HandlerController handlerController;
	private boolean isCancel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel socketChan, ByteBuffer buffer, QueueController queueController, HandlerController handlerController) {
		this.socketChannel = socketChan;
		this.buffer = buffer;
		this.queueController = queueController;
		this.handlerController = handlerController;
		this.isCancel = false;
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		// log out what received
		System.out.println("Bytes received: " + result.toString());
		
		// parsing data
		if (result > 0) {
		
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
			
			recvMsg = new String(bytes, StandardCharsets.UTF_8);
			System.out.println("Message received: " + recvMsg);
			attachment.setReturnMessage(recvMsg);
			this.setCommand(recvMsg);
			attachment.getActive().set(false);
			
			// process message
			try {
				Map<AsynchronousSocketChannel, String> msgToSend = processReturn(recvMsg, socketChannel);
				sendResponse(msgToSend);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else if (result == 0) {
			System.out.println("Client send empty message");
		} else {
			System.out.println("Connection closed on the client side");
		}

	}
	@Override
	public void failed(Throwable exc, Attachment attachment) {
		exc.printStackTrace();
	}
	
	private void setCommand(String inputMessage) {
		JSONObject parsedMsg = new JSONObject(inputMessage);
		this.cmd = Command.toCommand(parsedMsg.getString("command_code"));
		
	}
	
	public Command getCommand() {
		return this.cmd;
	}
	
	public HandlerController getHandlerController() {
		return this.handlerController;
	}
	
	public void setCancelSend(boolean isCancel) {
		this.isCancel = isCancel;
	}
	
	private void sendResponse(Map<AsynchronousSocketChannel, String> listMsg) {

		for (Map.Entry<AsynchronousSocketChannel, String> item : listMsg.entrySet()) {
			String msg = item.getValue();
			AsynchronousSocketChannel toSocket = item.getKey();
			Attachment newAttachment = new Attachment(msg, true);
			ByteBuffer bufferRequest = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));

			WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(toSocket);
			toSocket.write(bufferRequest, newAttachment, writeCompletionHandler);
			while (newAttachment.getActive().get()) {

			}
			handlerController.removeHandlerFromList(this);
			System.out.println("Done sending msg: " + msg);
			System.out.println("Is socket still open?: " + toSocket.isOpen());
		}
	}

	public Map<AsynchronousSocketChannel, String> processReturn(String recvMsg, AsynchronousSocketChannel sock)
			throws Exception {

		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();

		JSONObject clientMsg = new JSONObject(recvMsg);

		Command cmd = Command.toCommand(clientMsg.getString("command_code"));

		switch (cmd) {
			case LOGIN: {
				listResponse = this.processLoginRequest(recvMsg, sock);
				break;
			}
			case REGISTER: {
				listResponse = this.processRegisterRequest(recvMsg, sock);
				break;
			}
			case JOIN_QUEUE: {
				listResponse = this.processJoinQueueRequest(recvMsg, sock);
				break;
			}
			case QUIT_QUEUE: {
				listResponse = this.processQuitQueue(recvMsg, sock);
				break;
			}
			case MOVE: {
				listResponse = this.processMoveRequest(recvMsg, sock);
				break;
			}
			case LISTEN_MOVE:{
				listResponse = this.processListenMove(recvMsg, sock);
				break;
			}
			case DRAW_REQUEST: {
				listResponse = this.processRequestDrawRequest();
				break;
			}
			case DRAW_CONFIRM: {
				listResponse = this.processConfirmDrawRequest();
				break;
			}
			case ENDGAME: {
				listResponse = this.notifyEndgame();
				break;
			}
			case LEADERBOARD: {
				listResponse = this.processGetLeaderBoardRequest();
				break;
			}
			case CHAT: {
				listResponse = this.processChatRequest();
				break;
			}
			case CHATACK: {
				listResponse = this.processChatACKRequest();
				break;
			}
			case LOGOUT: {
				this.processLogoutRequest();
				break;
			}
			default: {
				listResponse = this.notifyUnknownCommand();
				break;
			}
		}

		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processLoginRequest(String input, AsynchronousSocketChannel sock)
			throws Exception {
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		LoginServerMessage serverResponse = null;

		LoginClientMessage clientRequest = new LoginClientMessage(input);
		String username = clientRequest.getUsername();
		String password = clientRequest.getPassword();
		// get logged player
		RankPlayer loggedPlayer = new T3Authenticator().login(username, password);
		if (loggedPlayer == null) {
			serverResponse = new LoginServerMessage("", "", 0, 0, 0, 0, 0, StatusCode.ERROR,
					"Username / Password is not valid");
		} else {
			loggedPlayer.setUserSocket(sock);
			queueController.pushToHall(loggedPlayer);
			serverResponse = new LoginServerMessage(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(),
					loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(),
					loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
		}
		listResponse.put(sock, serverResponse.toString());
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processRegisterRequest(String input, AsynchronousSocketChannel sock)
			throws Exception {
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		RegisterServerMessage serverResponse = null;

		RegisterClientMessage clientRequest = new RegisterClientMessage(input);
		String username = clientRequest.getUsername();

		String password = clientRequest.getPassword();
		// register new player
		RankPlayer loggedPlayer = new T3Authenticator().register(username, password);
		if (loggedPlayer == null) {
			serverResponse = new RegisterServerMessage("", "", 0, 0, 0, 0, 0, StatusCode.ERROR,
					"Username / Password is not valid");
		} else {
			loggedPlayer.setUserSocket(sock);
			queueController.pushToHall(loggedPlayer);
			serverResponse = new RegisterServerMessage(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(),
					loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(),
					loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
		}
		listResponse.put(sock, serverResponse.toString());
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processJoinQueueRequest(String input, AsynchronousSocketChannel sock)
			throws Exception {
		System.out.println("======== Start processing join queue request");

		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		JoinQueueClientMessage clientRequest = new JoinQueueClientMessage(input);
		String sessionID = clientRequest.getSessionID();
		String mode = clientRequest.getGameMode();
		if (mode.compareToIgnoreCase("normal") == 0) {
			// check if this is ranked user
			System.out.println("Normal request!");
			RankPlayer loggedPlayer = null;
			for (RankPlayer player : queueController.getHall()) {
				if (sessionID.compareTo(player.getSessionId()) == 0) {
					loggedPlayer = player;
					break;
				}
			}

			if (loggedPlayer != null) {
				queueController.removeFromHall(loggedPlayer);
				queueController.pushToQueue(loggedPlayer, mode);
//				while (normalQueue.size() < 2) {
//
//				}
//
//				Player player1 = normalQueue.poll();
//				Player player2 = normalQueue.poll();
//
//				ingameList.add(player1);
//				ingameList.add(player2);

				// prepare message according to each player
				Match match = null;
				boolean isFound = false;
				
				for(int i = 0; i < 30; i++) {	
					if(!isCancel) {
						for(Match m : queueController.getIngameList()) {
							if(m.isPlayerOfMatch(loggedPlayer)) {
								match = m;
								isFound = true;
								break;
							}
						}
						
						if(isFound) {
							break;
						} else {
							Thread.sleep(500);
						}
					} else {
						break;
					}
				}
				
				Player opponent = null;
				JoinQueueServerMessage serverResponse = null;
				if(isFound) {
					if(loggedPlayer.getUsername().equalsIgnoreCase(match.getPlayer1().getUsername())){
						// user is player 1
						opponent = match.getPlayer2();
					} else {
						// user is player 2
						opponent = match.getPlayer1();
					}
					serverResponse = new JoinQueueServerMessage(loggedPlayer.getUsername(), loggedPlayer.getSessionId(), opponent.getUsername(), 1234 /*mimic elo here*/, match.getMatchID(), match.getPlayer1().getUsername(), StatusCode.SUCCESS, "");
				} else if (!isFound){
					serverResponse = new JoinQueueServerMessage("", "", "", 0, -1, "", StatusCode.ERROR, "Cannot find appropiate match. Please try again later"); 
				} else if (isCancel) {
					serverResponse = new JoinQueueServerMessage("", "", "", 0, -1, "", StatusCode.ERROR, "QUIT_QUEUE sent from user");
				}
				 
				listResponse.put(sock, serverResponse.toString());
			} else {
				// if it is not ranked user, call to create a new guest player account

			}

		} else if (mode.compareToIgnoreCase("ranked") == 0) {

		} else {
			// error
		}
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> notifyMatchFound() throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		
		
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processMoveRequest(String input, AsynchronousSocketChannel sock)
			throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		MoveClientMessage moveMsg = new MoveClientMessage(input);

		int matchID = moveMsg.getMatchID();
		int x = moveMsg.getX();
		int y = moveMsg.getY();
		String movePlayer = moveMsg.getMovePlayer();
		String state = moveMsg.getState();
		String result = moveMsg.getResult();

		// find user with match id. Should add a field of match ID for player?
		// Nah, maybe query in database
		// only do a prototype here
		Match match = null;
		for (Match m : queueController.getIngameList()) {
			if (m.getMatchID() == matchID) {
				match = m;
				break;
			}
		}
		
		
		
		// should have a check here
		// but first, let's try to successfully send data to both clients
		
		String errMsg = "";
		StatusCode statCode = null;
		
		if(movePlayer.equalsIgnoreCase(match.getPlayer1().getUsername())) {
			if((match.getNumberOfMoves() % 2) == 0) {
				match.addNewMoveRecord(x, y, movePlayer, state, result);
				statCode = StatusCode.SUCCESS;
			} else {
				errMsg = "Invalid turn";
				statCode = StatusCode.ERROR;
			}
		} else {
			if((match.getNumberOfMoves() % 2) == 1) {
				match.addNewMoveRecord(x, y, movePlayer, state, result);
				statCode = StatusCode.SUCCESS;
			} else {
				errMsg = "Invalid turn";
				statCode = StatusCode.ERROR;
			}
		}

		MoveServerMessage fwdMsg = new MoveServerMessage(matchID, movePlayer, x, y, state, result, statCode, errMsg);

		listResponse.put(sock, fwdMsg.toString());

		return listResponse;
	}
	
	private Map<AsynchronousSocketChannel, String> processQuitQueue(String input, AsynchronousSocketChannel sock){
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		QuitQueueClientMessage quitQueueMsg = new QuitQueueClientMessage(input);
		
		boolean isOK = queueController.removeFromQueue(quitQueueMsg.getUsername());
		StatusCode statCode;
		String errMsg = "";
		
		if(isOK) {
			statCode = StatusCode.SUCCESS;
		} else {
			statCode = StatusCode.ERROR;
			errMsg = "An error occured when quiting queue";
		}
		
		QuitQueueServerMessage response = new QuitQueueServerMessage(quitQueueMsg.getUsername(), statCode, errMsg);
		// interrupt sending of the other message
		ReadCompletionHandler joinQueueHandler = handlerController.getCouple(this);
		joinQueueHandler.setCancelSend(false);
		
		// and send this msg
		listResponse.put(sock, response.toString());
		
		return listResponse;
	}
	

	private Map<AsynchronousSocketChannel, String> processListenMove(String input, AsynchronousSocketChannel sock){
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		// strategy: polling until there is a new move
		
		ListenMoveClientMessage listenMsg = new ListenMoveClientMessage(input);
		
		String username = listenMsg.getUsername();
		int matchID = listenMsg.getMatchID();
		Match match = null;
		
//		for(Match m : queueController.getIngameList()) {
//			if (m.getMatchID() == matchID) {
//				match = m;
//				break;
//			}
//		}
		
		Move latestMove;
		String movePlayer = "";
		while(true) {
			try {
				Thread.sleep(500);
				for(Match m : queueController.getIngameList()) {
					if (m.getMatchID() == matchID) {
						match = m;
						break;
					}
				}
				
				if(match.getNumberOfMoves() > 0) {				
					if(username.equalsIgnoreCase(match.getPlayer1().getUsername())) {
				
					if((match.getNumberOfMoves() % 2) == 0) {
						latestMove = match.getLatestMove();
						movePlayer = match.getPlayer2().getUsername();
						break;
					}
				} else {
					if((match.getNumberOfMoves() % 2 == 1)) {
						latestMove = match.getLatestMove();
						movePlayer = match.getPlayer1().getUsername();
						break;
					}
				}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		MoveServerMessage fwdMsg = new MoveServerMessage(matchID, movePlayer, latestMove.getX(), latestMove.getY(), latestMove.getState(), latestMove.getResult(), StatusCode.SUCCESS, "");
		listResponse.put(sock, fwdMsg.toString());
		return listResponse;
	}
	
	private Map<AsynchronousSocketChannel, String> processRequestDrawRequest() throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processConfirmDrawRequest() throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> notifyEndgame() throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processGetLeaderBoardRequest() throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processChatRequest() throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processChatACKRequest() throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		return listResponse;
	}

	private void processLogoutRequest() throws Exception {

	}

	private Map<AsynchronousSocketChannel, String> notifyUnknownCommand() throws Exception {
		// TODO: Finish the function here
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		return listResponse;
	}
	
}
