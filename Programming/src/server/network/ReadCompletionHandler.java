package server.network;

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
import message.ServerMessage;
import message.joinqueue.JoinQueueClientMessage;
import message.joinqueue.JoinQueueServerMessage;
import message.login.LoginClientMessage;
import message.login.LoginServerMessage;
import message.move.ListenMoveClientMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import message.register.RegisterClientMessage;
import message.register.RegisterServerMessage;
import protocol.Attachment;
import protocol.Command;
import protocol.StatusCode;
import server.authentication.T3Authenticator;
import server.process.QueueController;


public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{
	private final AsynchronousSocketChannel socketChannel;
	private final ByteBuffer buffer;
	private String recvMsg;
	private QueueController queueController;
	
	// Constructor
	
	public ReadCompletionHandler(AsynchronousSocketChannel socketChan, QueueController queueController, ByteBuffer buffer) {
		this.socketChannel = socketChan;
		this.buffer = buffer;
		this.queueController = queueController;
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
				while(true) {
					boolean isFound = false;
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
				}
				Player opponent = null;
				if(loggedPlayer.getUsername().equalsIgnoreCase(match.getPlayer1().getUsername())){
					// user is player 1
					opponent = match.getPlayer2();
				} else {
					// user is player 2
					opponent = match.getPlayer1();
				}
				JoinQueueServerMessage serverResponse = new JoinQueueServerMessage(loggedPlayer.getUsername(), loggedPlayer.getSessionId(), opponent.getUsername(), 1234 /*mimic elo here*/, match.getMatchID(), match.getPlayer1().getUsername(), StatusCode.SUCCESS, ""); 
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
