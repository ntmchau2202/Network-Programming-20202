package server.process;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
//import java.util.ArrayList;
import java.util.Queue;

import org.json.JSONObject;

import entity.Player.Player;
import entity.Player.RankPlayer;
import message.ClientMessage;
import message.joinqueue.JoinQueueClientMessage;
import message.joinqueue.JoinQueueServerMessage;
import message.login.LoginClientMessage;
import message.login.LoginServerMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import message.register.RegisterClientMessage;
import message.register.RegisterServerMessage;
import protocol.Attachment;
import protocol.Command;
import protocol.StatusCode;
import server.authentication.T3Authenticator;
import server.network.ReadCompletionHandler;
import server.network.WriteCompletionHandler;

public class Server {
	private AsynchronousServerSocketChannel serverSocketChannel;
	private ArrayList<RankPlayer> hall;
	private Queue<Player> normalQueue;
	private Queue<RankPlayer> rankedQueue;
	// temp only
	private ArrayList<Player> ingameList;

	public Server() throws Exception {
		serverSocketChannel = AsynchronousServerSocketChannel.open();
		hall = new ArrayList<RankPlayer>();
		normalQueue = new LinkedList<Player>();
		rankedQueue = new LinkedList<RankPlayer>();
		ingameList = new ArrayList<Player>();
	}

	public void start(int port) throws Exception {
		serverSocketChannel.bind(new InetSocketAddress(port));

		System.out.println("Server started");

		while(true) {
			serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>(){

				@Override
				public void completed(AsynchronousSocketChannel result, Object attachment) {
					// TODO Auto-generated method stub
					if ( serverSocketChannel.isOpen() ) {
						serverSocketChannel.accept(null, this);
						System.out.println("Connection accepted: " + result.toString() );
					}
					AsynchronousSocketChannel clientChannel = result;
					while(clientChannel != null && clientChannel.isOpen()) {
						ByteBuffer buffer = ByteBuffer.allocate(4096);
						ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(result, buffer);
						Attachment socketAttachment = new Attachment();
						clientChannel.read(buffer, socketAttachment, readCompletionHandler);
						while(socketAttachment.getActive().get()){

						}

						String recvMsg = socketAttachment.getReturnMessage();

						try {
							Map<AsynchronousSocketChannel, String> msgToSend = processReturn(recvMsg, result);
							sendResponse(msgToSend);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}

				@Override
				public void failed(Throwable exc, Object attachment) {
					// TODO Auto-generated method stub

				}

			});
			System.in.read();
			serverSocketChannel.close();
			System.out.println("Server done");
		}
	}

	private void sendResponse(Map<AsynchronousSocketChannel, String> listMsg) {

		for (Map.Entry<AsynchronousSocketChannel, String> item : listMsg.entrySet()) {
			String msg = item.getValue();
			AsynchronousSocketChannel toSocket = item.getKey();
			Attachment newAttachment = new Attachment(msg, true);
			ByteBuffer bufferRequest = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));

			WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(toSocket);
			toSocket.write(bufferRequest, newAttachment, writeCompletionHandler);
			while(newAttachment.getActive().get()) {

			}
			System.out.println("Done sending msg: " + msg);
		}

	}

	public Map<AsynchronousSocketChannel, String> processReturn(String recvMsg, AsynchronousSocketChannel sock) throws Exception {

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

	private Map<AsynchronousSocketChannel, String> processLoginRequest(String input, AsynchronousSocketChannel sock) throws Exception {
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		LoginServerMessage serverResponse = null;

		LoginClientMessage clientRequest = new LoginClientMessage(input);
		String username = clientRequest.getUsername();
		String password = clientRequest.getPassword();
		// get logged player
		RankPlayer loggedPlayer = new T3Authenticator().login(username, password);
		if (loggedPlayer == null) {
			serverResponse = new LoginServerMessage("", "", 0, 0, 0, 0, 0, StatusCode.ERROR, "Username / Password is not valid");
		} else {
			loggedPlayer.setUserSocket(sock);
			hall.add(loggedPlayer);
			serverResponse = new LoginServerMessage(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(), loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(), loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
		}
		listResponse.put(sock, serverResponse.toString());
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processRegisterRequest(String input, AsynchronousSocketChannel sock) throws Exception {
		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		RegisterServerMessage serverResponse = null;

		RegisterClientMessage clientRequest = new RegisterClientMessage(input);
		String username = clientRequest.getUsername();

		String password = clientRequest.getPassword();
		// register new player
		RankPlayer loggedPlayer = new T3Authenticator().register(username, password);
		if (loggedPlayer == null) {
			serverResponse = new RegisterServerMessage("", "", 0, 0, 0, 0, 0, StatusCode.ERROR, "Username / Password is not valid");
		} else {
			loggedPlayer.setUserSocket(sock);
			hall.add(loggedPlayer);
			serverResponse = new RegisterServerMessage(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(), loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(), loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
		}
		listResponse.put(sock, serverResponse.toString());
		return listResponse;
	}

	private Map<AsynchronousSocketChannel, String> processJoinQueueRequest(String input, AsynchronousSocketChannel sock) throws Exception {
		System.out.println("======== Start processing join queue request");

		Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
		JoinQueueClientMessage clientRequest = new JoinQueueClientMessage(input);
		String sessionID = clientRequest.getSessionID();
		String mode = clientRequest.getGameMode();
		if (mode.compareToIgnoreCase("normal") == 0) {
			// check if this is ranked user
			System.out.println("Normal request!");
			RankPlayer loggedPlayer = null;
			for (RankPlayer player : hall) {
				if (sessionID.compareTo(player.getSessionId())==0) {
					loggedPlayer = player;
					break;
				}
			}

			if (loggedPlayer != null) {
				hall.remove(loggedPlayer);
				normalQueue.add(loggedPlayer);
				System.out.println("normal queue: " + normalQueue.toString());
				while(normalQueue.size() < 2) {

				}

				Player player1 = normalQueue.poll();
				Player player2 = normalQueue.poll();

				ingameList.add(player1);
				ingameList.add(player2);

				// prepare message according to each player

				JoinQueueServerMessage player1Message = new JoinQueueServerMessage(player1.getUsername(), player1.getSessionId(), player2.getUsername(), 1234, 1234, player1.getUsername(), StatusCode.SUCCESS, "");
				JoinQueueServerMessage player2Message = new JoinQueueServerMessage(player2.getUsername(), player2.getSessionId(), player1.getUsername(), 1234, 1234, player1.getUsername(), StatusCode.SUCCESS, "");

				listResponse.put(player1.getUserSocket(), player1Message.toString());
				listResponse.put(player2.getUserSocket(), player2Message.toString());
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

	private Map<AsynchronousSocketChannel, String> processMoveRequest(String input, AsynchronousSocketChannel sock) throws Exception {
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
		Player partner = null;
		for(Player p : ingameList) {
			if (p.getUsername().compareToIgnoreCase(movePlayer) != 0) {
				partner = p;
				break;
			}
		}

		MoveServerMessage fwdMsg = new MoveServerMessage(matchID, movePlayer, x, y, state, result, StatusCode.SUCCESS, "");

		listResponse.put(sock, fwdMsg.toString());
		listResponse.put(partner.getUserSocket(), fwdMsg.toString());


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