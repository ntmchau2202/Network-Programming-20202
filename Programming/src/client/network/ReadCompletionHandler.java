package client.network;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

import helper.MessageParser;
import protocol.Attachment;
import protocol.Command;
import protocol.StatusCode;

public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{

	private final ByteBuffer inputBuffer;
	private MessageParser msgParser;
	private String recvMsg;
	private String errorMsg;
	private StatusCode statusCode;
	
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
	
	private boolean checkResponse() {
		statusCode = msgParser.getStatusCode(recvMsg);
		errorMsg = (String)msgParser.getErrorMessage(recvMsg);
		
		if (statusCode.compareTo(StatusCode.ERROR) == 0) {
			return false;
		} 
		
		return true;
		
	}
	
	private void processLoginResponse() {
		if ( this.checkResponse() ) {
			// TODO: Finish function for success case
			String username = (String)msgParser.getInfoField(recvMsg, "username");
			String sessionID = (String)msgParser.getInfoField(recvMsg, "session_id");
			int elo = (int)msgParser.getInfoField(recvMsg, "elo");
			
		} else {
			// TODO: Finish function for error case
		}
	}
	
	private void processRegisterResponse() {
		if ( this.checkResponse() ) {
			String username = (String)msgParser.getInfoField(recvMsg, "username");
			String sessionID = (String)msgParser.getInfoField(recvMsg, "session_id");
			int elo = (int)msgParser.getInfoField(recvMsg, "elo");
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}
		
	}
	
	private void processJoinQueueResponse() {
		if ( this.checkResponse() ) {
			String username = (String)msgParser.getInfoField(recvMsg, "username");
			String sessionID = (String)msgParser.getInfoField(recvMsg, "session_id");
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}		
	}
	
	private void processMatchFoundResponse() {
		if ( this.checkResponse() ) {
			int matchID = (int)msgParser.getInfoField(recvMsg,"match_id");
			String opponent = (String)msgParser.getInfoField(recvMsg, "opponent");
			int elo = (int)msgParser.getInfoField(recvMsg, "elo");
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}		
	}
	
	private void processMoveResponse() {
		if ( this.checkResponse() ) {
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}
	}
	
	private void processEndgameResponse() {
		if ( this.checkResponse() ) {
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}
	}
	
	private void processRequestDrawResponse() {
		if ( this.checkResponse() ) {
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}
	}
	
	private void processConfirmDrawResponse() {
		if ( this.checkResponse() ) {
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}
	}
	
	private void processGetLeaderBoardResponse() {
		if ( this.checkResponse() ) {
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}
	}
	
	private void processChatResponse() {
		if ( this.checkResponse() ) {
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}
	}
	
	private void processChatACKResponse() {
		if ( this.checkResponse() ) {
			// TODO: Finish function for success case
					
		} else {
			// TODO: Finish function for error case
		}
	}
	
	private void notifyUnknownCommand() {
		// TODO: notify unknown command here
		System.out.println("Unknown command");
	}

}
