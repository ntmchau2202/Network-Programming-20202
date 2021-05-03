package AsynchronousClient;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{

	private final ByteBuffer inputBuffer;
	
	// constructor
	public ReadCompletionHandler(ByteBuffer buf) {
		this.inputBuffer = buf;
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		// logging
		System.out.println("Bytes read: " + result.toString());
		
		// read message
		try {
			inputBuffer.flip();
			System.out.println("Echo client received: " + StandardCharsets.UTF_8.newDecoder().decode(inputBuffer));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		exc.printStackTrace();
		
	}
	
}
