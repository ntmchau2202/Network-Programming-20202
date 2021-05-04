package client.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

import protocol.Attachment;

public class AcceptCompletionHandler implements CompletionHandler<Void, Attachment> {
	private final AsynchronousSocketChannel socketChannel;
	
	public AcceptCompletionHandler(AsynchronousSocketChannel sockChannel) {
		this.socketChannel = sockChannel;
	}

	@Override
	public void completed(Void result, Attachment attachment) {
		// print out connection
		System.out.println("Connection accepted: " + socketChannel.toString());
		
		String message = attachment.getMessage();
		System.out.println("Message to be sent: "+message);
		
		ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
		WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
		socketChannel.write(buffer, attachment, writeCompletionHandler);
		
		
	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		exc.printStackTrace();
		
	}
	
}
