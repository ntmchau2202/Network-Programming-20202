package server.core;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;
//import java.util.ArrayList;

import protocol.Attachment;
import server.core.controller.CompletionHandlerController;
import server.core.controller.QueueController;
import server.core.logger.T3Logger;
import server.entity.network.completionHandler.ReadCompletionHandler;

public class ServerCore {
    public static Logger LOGGER = T3Logger.getLogger(ServerCore.class.getName());

    private AsynchronousServerSocketChannel serverSocketChannel;
    private QueueController queueController;

    public ServerCore() throws Exception {
        serverSocketChannel = AsynchronousServerSocketChannel.open();
    }

    public void start(int port) throws Exception {
        serverSocketChannel.bind(new InetSocketAddress(port));

        LOGGER.info("Server started");

        queueController = new QueueController();
        queueController.startQueueController();


        while (true) {
            serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    CompletionHandlerController completionHandlerController = new CompletionHandlerController();

                    if (serverSocketChannel.isOpen()) {
                        serverSocketChannel.accept(null, this);
                        LOGGER.info("Connection accepted: " + result.toString());
                    }
                    AsynchronousSocketChannel clientChannel = result;
                    try {
                        while (clientChannel != null && clientChannel.isOpen()) {
                            // init buffer
                            ByteBuffer buffer = ByteBuffer.allocate(4096);
                            buffer.clear();

                            // init handler & attachment
                            ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(result, buffer, queueController, completionHandlerController);
                            Attachment socketAttachment = new Attachment();

                            // start listening from socket
                            clientChannel.read(buffer, socketAttachment, readCompletionHandler);

                            // add current handler to current client's handler controller
                            completionHandlerController.addToListController(readCompletionHandler);

//                            LOGGER.info("Listening for more messages from client: " + completionHandlerController.getHandlerCtrlID());
                            // when current handler parse new message successfully -> this `while` loop is exited
                            // -> init new handler for new message
                            while (socketAttachment.getActive().get()) {

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

                    } catch (Exception connException) {
                        System.out.println("this is my exception: " + connException.toString());
                    }

                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    // TODO Auto-generated method stub
                    System.out.println("Hmmmmm we cannot accept the connection");
                }

            });
            System.in.read();
            serverSocketChannel.close();
            System.out.println("Server done");
        }
    }


}