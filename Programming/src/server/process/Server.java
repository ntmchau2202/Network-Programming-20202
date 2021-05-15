package server.process;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;

import entity.Player.Player;
import entity.Player.RankPlayer;
import helper.MessageParser;
import protocol.Attachment;
import protocol.Command;
import protocol.ServerMessage;
import protocol.StatusCode;
import server.authentication.T3Authenticator;
import server.network.ReadCompletionHandler;
import server.network.WriteCompletionHandler;

public class Server {
	private AsynchronousServerSocketChannel serverSocketChannel;
//	private Attachment attachment;
//	private ArrayList<AsynchronousSocketChannel> listSocket;
	private MessageParser msgParser = new MessageParser();
	
	public Server() throws Exception {
		serverSocketChannel = AsynchronousServerSocketChannel.open();
//		attachment = new Attachment();
//		listSocket = new ArrayList<AsynchronousSocketChannel>();
	}

	public void start(int port) throws Exception {
		serverSocketChannel.bind(new InetSocketAddress(port));
		
		System.out.println("Server started");
		
//		AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(serverSocketChannel, attachment, listSocket);
//		serverSocketChannel.accept(attachment, acceptCompletionHandler);
		
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
		
		Command cmd = msgParser.getCommand(recvMsg);
		switch (cmd) {
		case LOGIN: {
				response = this.processLoginRequest(recvMsg);
				break;
			}
			case REGISTER: {
				response = this.processRegisterRequest();
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
		ServerMessage serverResponse = new ServerMessage();
		String username = (String)msgParser.getInfoField(input, "username");
		String password = (String)msgParser.getInfoField(input, "password");
		// get logged player
		RankPlayer loggedPlayer = new T3Authenticator().login(username, password);
		if (loggedPlayer == null) {
			serverResponse.createLoginResponse("", "", 0, StatusCode.ERROR, "Username / Password is invalid");
		} else {
			serverResponse.createLoginResponse(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(), StatusCode.SUCCESS, "");
		}
    	return serverResponse.toString();		
	}
	
	private String processRegisterRequest() throws Exception {
		// TODO: Finish the function here
		String returnMsg = "";
		return returnMsg;
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
