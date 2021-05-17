package server.process;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;

import org.json.JSONObject;

import entity.Player.Player;
import entity.Player.RankPlayer;
import message.ClientMessage;
import message.login.LoginClientMessage;
import message.login.LoginServerMessage;
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
	
	public Server() throws Exception {
		serverSocketChannel = AsynchronousServerSocketChannel.open();
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
			    			String msgToSend = processReturn(recvMsg);
			    			sendResponse(msgToSend, result);
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
	
	private void sendResponse(String msg, AsynchronousSocketChannel toSocket) {

    	Attachment newAttachment = new Attachment(msg, true);
    	ByteBuffer bufferRequest = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
    	
    	WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(toSocket);
		toSocket.write(bufferRequest, newAttachment, writeCompletionHandler);
		while(newAttachment.getActive().get()) {
			
		}
		System.out.println("Done sending");	
	}
	
	public String processReturn(String recvMsg) throws Exception {
		String response = "";
		
		JSONObject clientMsg = new JSONObject(recvMsg);
		
		Command cmd = Command.toCommand(clientMsg.getString("command_code"));
		
		switch (cmd) {
		case LOGIN: {
				response = this.processLoginRequest(recvMsg);
				break;
			}
			case REGISTER: {
				response = this.processRegisterRequest(recvMsg);
				break;
			}
			case JOIN_QUEUE: {
				response = this.processJoinQueueRequest();
				break;
			}
			case MATCH_FOUND: {
				response = this.notifyMatchFound();
				break;
			}
			case MOVE: {
				response = this.processMoveRequest();
				break;
			}
			case DRAW_REQUEST: {
				response = this.processRequestDrawRequest();
				break;
			}
			case DRAW_CONFIRM: {
				response = this.processConfirmDrawRequest();
				break;
			}
			case ENDGAME: {
				response = this.notifyEndgame();
				break;
			}
			case LEADERBOARD: {
				response = this.processGetLeaderBoardRequest();
				break;
			}
			case CHAT: {
				response = this.processChatRequest();
				break;
			}
			case CHATACK: {
				response = this.processChatACKRequest();
				break;
			}
			case LOGOUT: {
				this.processLogoutRequest();
				break;
			}
			default: {
				response = this.notifyUnknownCommand();
				break;
			}
		}
		
		return response;
	}
	
	private String processLoginRequest(String input) throws Exception {

		LoginServerMessage serverResponse = null;
		
		LoginClientMessage clientRequest = new LoginClientMessage(input);
		String username = clientRequest.getUsername();
		String password = clientRequest.getPassword();
		// get logged player
		RankPlayer loggedPlayer = new T3Authenticator().login(username, password);
		if (loggedPlayer == null) {
			serverResponse = new LoginServerMessage("", "", 0, 0, 0, 0, 0, StatusCode.ERROR, "Username / Password is not valid");
		} else {
			serverResponse = new LoginServerMessage(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(), loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(), loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
		}
    	return serverResponse.toString();		
	}
	
	private String processRegisterRequest(String input) throws Exception {
		RegisterServerMessage serverResponse = null;
		
		RegisterClientMessage clientRequest = new RegisterClientMessage(input);
		String username = clientRequest.getUsername();
		
		String password = clientRequest.getPassword();
		// register new player
		RankPlayer loggedPlayer = new T3Authenticator().register(username, password);
		if (loggedPlayer == null) {
			serverResponse = new RegisterServerMessage("", "", 0, 0, 0, 0, 0, StatusCode.ERROR, "Username / Password is not valid");
		} else {
			serverResponse = new RegisterServerMessage(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(), loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(), loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
		}
		return serverResponse.toString();
	}
	
	private String processJoinQueueRequest() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;
	}
	
	private String notifyMatchFound() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;
	}
	
	private String processMoveRequest() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;		
	}
	
	private String processRequestDrawRequest() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;		
	}
	
	private String processConfirmDrawRequest() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;		
	}
	
	private String notifyEndgame() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;		
	}
	
	private String processGetLeaderBoardRequest() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;		
	}
	
	private String processChatRequest() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;		
	}
	
	private String processChatACKRequest() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;		
	}
	
	private void processLogoutRequest() throws Exception {
		
	}
	
	private String notifyUnknownCommand() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;		
	}
}
