package server.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

import protocol.ServerMessage;
import protocol.StatusCode;

public class ReadCompletionHandler implements CompletionHandler<Integer, Void>{
	private final AsynchronousSocketChannel socketChannel;
	private final ByteBuffer buffer;
	
	// Constructor
	
	public ReadCompletionHandler(AsynchronousSocketChannel socketChan, ByteBuffer buf) {
		this.socketChannel = socketChan;
		this.buffer = buf;
		
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
			
			String msg = new String(bytes, StandardCharsets.UTF_8);
			System.out.println("Message received: " + msg);
			
			// do some consuming work here
			
			// and prepare the message
			ServerMessage loginResponse = new ServerMessage();
			loginResponse.createLoginResponse("hikaru", "AOUJBSGOAW01p39u5P", 15000, StatusCode.SUCCESS, "");
			String msgToSend = loginResponse.toString();
			ByteBuffer bufferRespone = ByteBuffer.wrap(msgToSend.getBytes(StandardCharsets.UTF_8));
			
			WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
			socketChannel.write(bufferRespone, null, writeCompletionHandler);
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
	
	
}
