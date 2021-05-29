package client.network;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import protocol.Attachment;


public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{

private final ByteBuffer inputBuffer;
	
	// constructor
	public ReadCompletionHandler(ByteBuffer buf) {
		this.inputBuffer = buf;
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		// logging
		// System.out.println("Bytes read: " + result.toString());
		
		// read message
		try {
			if (result > 0) {
				inputBuffer.flip();
				String tmp = StandardCharsets.UTF_8.newDecoder().decode(inputBuffer).toString();
				attachment.setReturnMessage(tmp);
				attachment.getActive().set(false);
			}			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		exc.printStackTrace();
		
	}

}
