package server.entity.network.completionHandler;

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
import server.core.authentication.T3Authenticator;
import server.core.controller.CompletionHandlerController;
import server.core.controller.QueueController;


public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{
	private final AsynchronousSocketChannel socketChannel;
	private final ByteBuffer buffer;
	private String recvMsg;
	private CompletionHandlerController completionHandlerController;
	private boolean isCancel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel socketChan, ByteBuffer buffer, QueueController queueController, CompletionHandlerController completionHandlerController) {
		this.socketChannel = socketChan;
		this.buffer = buffer;
		this.queueController = queueController;
		this.completionHandlerController = completionHandlerController;
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
	
	public CompletionHandlerController getHandlerController() {
		return this.completionHandlerController;
	}
	
	public void setCancelSend(boolean isCancel) {
		this.isCancel = isCancel;
	}
	

	
}
