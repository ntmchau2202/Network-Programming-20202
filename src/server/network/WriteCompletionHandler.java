package server.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, Void>{
	private final AsynchronousSocketChannel socketChannel;

	// constructor
	
	public WriteCompletionHandler(AsynchronousSocketChannel socketChan) {
		this.socketChannel = socketChan;
	}
	
	@Override
	public void completed(Integer result, Void attachment) {
		System.out.println("Server written "+result.toString()+ " bytes");
    	ByteBuffer buffer = ByteBuffer.allocate(4096);
		ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, buffer);
		socketChannel.read(buffer, null, readCompletionHandler);
	}

	@Override
	public void failed(Throwable exc, Void attachment) {
		exc.printStackTrace();
	}
	
	
}
