package AsynchronousClient;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, Attachment> {

	private final AsynchronousSocketChannel socketChannel;

	public WriteCompletionHandler(AsynchronousSocketChannel sockChannel) {
		this.socketChannel = sockChannel;
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		System.out.println("Client written " + result.toString() + " bytes");
		
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(buffer);
		socketChannel.read(buffer, attachment, readCompletionHandler);
		
	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		exc.printStackTrace();
		
	}

}
