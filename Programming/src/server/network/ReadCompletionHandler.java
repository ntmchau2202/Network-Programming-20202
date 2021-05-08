package server.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

import helper.MessageParser;
import protocol.Command;
import protocol.ServerMessage;
import protocol.StatusCode;

public class ReadCompletionHandler implements CompletionHandler<Integer, Void>{
	private final AsynchronousSocketChannel socketChannel;
	private final ByteBuffer buffer;
	private MessageParser msgParser;
	private String recvMsg;
	private ServerMessage serverResponse;
	
	// Constructor
	
	public ReadCompletionHandler(AsynchronousSocketChannel socketChan, ByteBuffer buf) {
		this.socketChannel = socketChan;
		this.buffer = buf;
		this.msgParser = new MessageParser();
		this.serverResponse = new ServerMessage();
	}
	
	@Override
	public void completed(Integer result, Void attachment) {
		// log out what received
		System.out.println("Bytes received: " + result.toString());
		
		// parsing data
		if (result > 0) {
		
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
			
			recvMsg = new String(bytes, StandardCharsets.UTF_8);
			System.out.println("Message received: " + recvMsg);
			
			// this is where the magic begin
			
			Command cmd = msgParser.getCommand(recvMsg);
			System.out.println("Command is: " + cmd.getCommandString());
			
			switch (cmd) {
				case LOGIN: {
					this.processLoginRequest();
					break;
				}
				case REGISTER: {
					this.processRegisterRequest();
					break;
				}
				case JOIN_QUEUE: {
					this.processJoinQueueRequest();
					break;
				}
				case MATCH_FOUND: {
					this.notifyMatchFound();
					break;
				}
				case MOVE: {
					this.processMoveRequest();
					break;
				}
				case DRAW_REQUEST: {
					this.processRequestDrawRequest();
					break;
				}
				case DRAW_CONFIRM: {
					this.processConfirmDrawRequest();
					break;
				}
				case ENDGAME: {
					this.notifyEndgame();
					break;
				}
				case LEADERBOARD: {
					this.processGetLeaderBoardRequest();
					break;
				}
				case CHAT: {
					this.processChatRequest();
					break;
				}
				case CHATACK: {
					this.processChatACKRequest();
					break;
				}
				default: {
					this.notifyUnknownCommand();
					break;
				}
			}
			
			
		} else if (result == 0) {
			System.out.println("Client send empty message");
		} else {
			System.out.println("Connection closed on the client side");
		}

	}
	@Override
	public void failed(Throwable exc, Void attachment) {
		exc.printStackTrace();
	}
	
	public void sendResponse() {
		String msgToSend = serverResponse.toString();
		ByteBuffer bufferRequest = ByteBuffer.wrap(msgToSend.getBytes(StandardCharsets.UTF_8));
		
		WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
		socketChannel.write(bufferRequest, null, writeCompletionHandler);
	}
	
	// ================== LOGICAL PROCESSES ========================
	
		private void processLoginRequest() {
			// for testing purpose only
			String username = (String)msgParser.getInfoField(recvMsg, "username");
			String password = (String)msgParser.getInfoField(recvMsg, "password");

			System.out.println("Username received: " + username);
			System.out.println("Password received: " + password);
			
			// TODO: Finish function
			
			// and prepare the message
			serverResponse.createLoginResponse("hikaru", "AOUJBSGOAW01p39u5P", 15000, StatusCode.SUCCESS, "");
			sendResponse();
		}
		
		private void processRegisterRequest() {
			String username = (String)msgParser.getInfoField(recvMsg, "username");
			String password = (String)msgParser.getInfoField(recvMsg, "password");
			
			// TODO: Finish function
			
			serverResponse.createRegisterResponse();
			sendResponse();
		}
		
		private void processJoinQueueRequest() {
			
			String mode = (String)msgParser.getInfoField(recvMsg, "mode");
			String sessionID = (String)msgParser.getInfoField(recvMsg, "session_id");
			
			// TODO: Finish function
			
			serverResponse.createJoinQueueResponse();
			sendResponse();
			
		}
		
		private void notifyMatchFound() {
			serverResponse.createMatchFoundRespone();
			sendResponse();
		}
		
		private void processMoveRequest() {
			// TODO: Finish function
		}
		
		private void notifyEndgame() {
			// TODO: Finish function
			serverResponse.createEndgameResponse();
			sendResponse();
		}
		
		private void processRequestDrawRequest() {
			// TODO: Finish function
		}
		
		private void processConfirmDrawRequest() {
			// TODO: Finish function
		}
		
		private void processGetLeaderBoardRequest() {
			// TODO: Finish function
		}
		
		private void processChatRequest() {
			// TODO: Finish function
		}
		
		private void processChatACKRequest() {
			// TODO: Finish function
		}
		
		private void processLogoutRequest() {
			// TODO: Finish function
		}
		
		private void notifyUnknownCommand() {
			System.out.println("Unknown command");
		}
	
	
}
