package server.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

    private final AsynchronousServerSocketChannel serverSocketChannel;
    
    // Constructor
    public AcceptCompletionHandler(AsynchronousServerSocketChannel svSocketChannel) {
    	this.serverSocketChannel = svSocketChannel;
    }
    
    @Override
	public void completed(AsynchronousSocketChannel result, Void attachment) {
		// notify there is a connection
    	System.out.println("Connection accepted: "+ result.toString());
    	
    	serverSocketChannel.accept(null, this);
		
    	// start to read
    	ByteBuffer buffer = ByteBuffer.allocate(4096);
    	// we have to use another completion handler here for reading message
    	
    	ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(result, buffer);
    	result.read(buffer, null, readCompletionHandler);
	}

	@Override
	public void failed(Throwable exc, Void attachment) {
		exc.printStackTrace();
	}

}
