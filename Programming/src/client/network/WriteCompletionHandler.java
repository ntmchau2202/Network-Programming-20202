package client.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import protocol.Attachment;

public class WriteCompletionHandler implements CompletionHandler<Integer, Attachment> {

	private final AsynchronousSocketChannel socketChannel;

	public WriteCompletionHandler(AsynchronousSocketChannel sockChannel) {
		this.socketChannel = sockChannel;
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		System.out.println("Client written " + result.toString() + " bytes");
		System.out.println("Message sent from client: " + attachment.getSendMessage());
		
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(buffer);
		try {
			socketChannel.read(buffer, attachment, readCompletionHandler);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Pending sending...");
			socketChannel.read(buffer, attachment, readCompletionHandler);
		}
		
		
	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		System.out.println("Failed from WriteCompletionHandler for string: " + attachment.getSendMessage());
		exc.printStackTrace();
		
	}

}
