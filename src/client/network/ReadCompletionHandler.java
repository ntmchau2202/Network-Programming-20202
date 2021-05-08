package client.network;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

import helper.MessageParser;
import protocol.Attachment;
import protocol.Command;

public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{

	private final ByteBuffer inputBuffer;
	private MessageParser msgParser;
	private String recvMsg;
	
	// constructor
	public ReadCompletionHandler(ByteBuffer buf) {
		this.inputBuffer = buf;
		msgParser = new MessageParser();
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		// logging
		System.out.println("Bytes read: " + result.toString());
		
		// read message
		try {
			inputBuffer.flip();
			recvMsg = StandardCharsets.UTF_8.newDecoder().decode(inputBuffer).toString();
			System.out.println("Echo client received: " + recvMsg);
			// this is where the magic begin
			Command cmd = msgParser.getCommand(recvMsg);
			System.out.println("Command is: " + cmd.getCommandString());
			
			switch (cmd) {
				case LOGIN: {
					this.processLoginResponse();
					break;
				}
				case REGISTER: {
					this.processRegisterResponse();
					break;
				}
				case JOIN_QUEUE: {
					this.processJoinQueueResponse();
					break;
				}
				case MATCH_FOUND: {
					this.processMatchFoundResponse();
					break;
				}
				case MOVE: {
					this.processMoveResponse();
					break;
				}
				case DRAW_REQUEST: {
					this.processRequestDrawResponse();
					break;
				}
				case DRAW_CONFIRM: {
					this.processConfirmDrawResponse();
					break;
				}
				case ENDGAME: {
					this.processEndgameResponse();
					break;
				}
				case LEADERBOARD: {
					this.processGetLeaderBoardResponse();
					break;
				}
				case CHAT: {
					this.processChatResponse();
					break;
				}
				case CHATACK: {
					this.processChatACKResponse();
					break;
				}
				default: {
					this.notifyUnknownCommand();
					break;
				}
			}

			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		exc.printStackTrace();
		
	}
	
	// ================== LOGICAL PROCESSES ========================
	
	private void processLoginResponse() {
		// for testing purpose only
		String username = (String)msgParser.getInfoField(recvMsg, "username");
		String sessionID = (String)msgParser.getInfoField(recvMsg, "session_id");
		int elo = (int)msgParser.getInfoField(recvMsg, "elo");
		System.out.println("Username is: " + username);
		System.out.println("session_id is: "+ sessionID);
		System.out.println("elo of user is: " + Integer.toString(elo));
		
		// TODO: Finish function
	}
	
	private void processRegisterResponse() {
		// TODO: Finish function
	}
	
	private void processJoinQueueResponse() {
		// TODO: Finish function
	}
	
	private void processMatchFoundResponse() {
		// TODO: Finish function
	}
	
	private void processMoveResponse() {
		// TODO: Finish function
	}
	
	private void processEndgameResponse() {
		// TODO: Finish function
	}
	
	private void processRequestDrawResponse() {
		// TODO: Finish function
	}
	
	private void processConfirmDrawResponse() {
		// TODO: Finish function
	}
	
	private void processGetLeaderBoardResponse() {
		// TODO: Finish function
	}
	
	private void processChatResponse() {
		// TODO: Finish function
	}
	
	private void processChatACKResponse() {
		// TODO: Finish function
	}
	
	private void notifyUnknownCommand() {
		System.out.println("Unknown command");
	}

}
