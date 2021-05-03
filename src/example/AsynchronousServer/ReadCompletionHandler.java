package AsynchronousServer;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

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
		if (result >= 0) {
		
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
			
			String msg = new String(bytes, StandardCharsets.UTF_8);
			System.out.println("Message received: " + msg);
			
			// do work here
			// temporary, echo
			
			WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
			buffer.flip();
			socketChannel.write(buffer, null, writeCompletionHandler);
		} else {
			System.out.println("Connection closed on the client side");
		}

	}
	@Override
	public void failed(Throwable exc, Void attachment) {
		exc.printStackTrace();
	}
	
	
}
