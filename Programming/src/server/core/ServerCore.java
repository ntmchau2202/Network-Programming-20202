package server.core;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
//import java.util.ArrayList;

import protocol.Attachment;
import server.core.controller.CompletionHandlerController;
import server.core.controller.QueueController;
import server.entity.network.completionHandler.ReadCompletionHandler;

public class ServerCore {
	private AsynchronousServerSocketChannel serverSocketChannel;
	private QueueController queueController;

	public ServerCore() throws Exception {
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
					CompletionHandlerController completionHandlerController = new CompletionHandlerController();
					
					if (serverSocketChannel.isOpen()) {
						serverSocketChannel.accept(null, this);
						System.out.println("Connection accepted: " + result.toString());
					}
					AsynchronousSocketChannel clientChannel = result;
					while (clientChannel != null && clientChannel.isOpen()) {
						ByteBuffer buffer = ByteBuffer.allocate(4096);
						buffer.clear();
			    		ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(result, buffer, queueController, completionHandlerController);
			    		Attachment socketAttachment = new Attachment();
			    		clientChannel.read(buffer, socketAttachment, readCompletionHandler);
			    		completionHandlerController.addToListController(readCompletionHandler);
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