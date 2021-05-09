package server.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

import protocol.Attachment;
import protocol.ServerMessage;


public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{
	private final AsynchronousSocketChannel socketChannel;
	private final ByteBuffer buffer;
	private String recvMsg;
	private ServerMessage serverResponse;
	
	// Constructor
	
	public ReadCompletionHandler(AsynchronousSocketChannel socketChan, ByteBuffer buf) {
		this.socketChannel = socketChan;
		this.buffer = buf;
		this.serverResponse = new ServerMessage();
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
			
			// this is where the magic begin
			
//			Command cmd = msgParser.getCommand(recvMsg);
//			System.out.println("Command is: " + cmd.getCommandString());
//			
//			switch (cmd) {
//				case LOGIN: {
//					this.processLoginRequest();
//					break;
//				}
//				case REGISTER: {
//					this.processRegisterRequest();
//					break;
//				}
//				case JOIN_QUEUE: {
//					this.processJoinQueueRequest();
//					break;
//				}
//				case MATCH_FOUND: {
//					this.notifyMatchFound();
//					break;
//				}
//				case MOVE: {
//					this.processMoveRequest();
//					break;
//				}
//				case DRAW_REQUEST: {
//					this.processRequestDrawRequest();
//					break;
//				}
//				case DRAW_CONFIRM: {
//					this.processConfirmDrawRequest();
//					break;
//				}
//				case ENDGAME: {
//					this.notifyEndgame();
//					break;
//				}
//				case LEADERBOARD: {
//					this.processGetLeaderBoardRequest();
//					break;
//				}
//				case CHAT: {
//					this.processChatRequest();
//					break;
//				}
//				case CHATACK: {
//					this.processChatACKRequest();
//					break;
//				}
//				default: {
//					this.notifyUnknownCommand();
//					break;
//				}
//			}
			
			
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
	
	public void sendResponse() {
		String msgToSend = serverResponse.toString();
		ByteBuffer bufferRequest = ByteBuffer.wrap(msgToSend.getBytes(StandardCharsets.UTF_8));
		
		WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
		socketChannel.write(bufferRequest, null, writeCompletionHandler);
	}
	
}
