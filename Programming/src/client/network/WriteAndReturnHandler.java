package client.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import protocol.Attachment;

public class WriteAndReturnHandler implements CompletionHandler<Integer, Attachment> {
	private final AsynchronousSocketChannel socketChannel;

	public WriteAndReturnHandler(AsynchronousSocketChannel sockChannel) {
		this.socketChannel = sockChannel;
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		System.out.println("Client written " + result.toString() + " bytes");
		System.out.println("Message sent from client: " + attachment.getSendMessage());	
		
	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		System.out.println("Failed from WriteCompletionHandler for string: " + attachment.getSendMessage());
		exc.printStackTrace();
		
	}

}
