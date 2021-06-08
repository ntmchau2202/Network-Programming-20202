package server.network.completionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import protocol.Attachment;

public class WriteCompletionHandler implements CompletionHandler<Integer, Attachment>{
//	private final AsynchronousSocketChannel socketChannel;

	// constructor
	
	public WriteCompletionHandler(AsynchronousSocketChannel socketChan) {
//		this.socketChannel = socketChan;
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		System.out.println("Server written "+result.toString()+ " bytes");
    	// ByteBuffer buffer = ByteBuffer.allocate(4096);
    	attachment.getActive().set(false);
//		ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, buffer);
//		Attachment newAttachment = new Attachment();
//		socketChannel.read(buffer, newAttachment, readCompletionHandler);
	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		exc.printStackTrace();
	}
	
	
}
