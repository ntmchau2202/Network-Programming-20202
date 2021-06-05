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

import entity.Match.Match;
import entity.Move.Move;
import entity.Player.Player;
import entity.Player.RankPlayer;
import javafx.util.Pair;
import message.ClientMessage;
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
import server.network.ReadCompletionHandler;
import server.network.WriteCompletionHandler;

public class Server {
	private AsynchronousServerSocketChannel serverSocketChannel;
	private QueueController queueController;

	public Server() throws Exception {
		serverSocketChannel = AsynchronousServerSocketChannel.open();
	}

	public void start(int port) throws Exception {
		serverSocketChannel.bind(new InetSocketAddress(port));

		System.out.println("Server started");
		
		queueController = new QueueController();
		queueController.startQueueController();
		

		while (true) {
			serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

				@Override
				public void completed(AsynchronousSocketChannel result, Object attachment) {
					// TODO Auto-generated method stub
					if (serverSocketChannel.isOpen()) {
						serverSocketChannel.accept(null, this);
						System.out.println("Connection accepted: " + result.toString());
					}
					AsynchronousSocketChannel clientChannel = result;
					while (clientChannel != null && clientChannel.isOpen()) {
						ByteBuffer buffer = ByteBuffer.allocate(4096);
						buffer.clear();
			    		ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(result, queueController, buffer);
			    		Attachment socketAttachment = new Attachment();
			    		clientChannel.read(buffer, socketAttachment, readCompletionHandler);
			    		System.out.println("Listening...");
			    		while(socketAttachment.getActive().get()){

			    		}
//			    		System.out.println("Got msg");
//			    		String recvMsg = socketAttachment.getReturnMessage();
//			    		
//			    		try {
//			    			Map<AsynchronousSocketChannel, String> msgToSend = processReturn(recvMsg, result);
//			    			sendResponse(msgToSend);
//			    		} catch (Exception e) {
//			    			e.printStackTrace();
//			    		}		    		
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

	
}