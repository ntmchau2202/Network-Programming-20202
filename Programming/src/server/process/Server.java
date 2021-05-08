package server.process;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import server.network.AcceptCompletionHandler;

public class Server {
	private AsynchronousServerSocketChannel serverSocketChannel;
	
	public Server() throws Exception {
		serverSocketChannel = AsynchronousServerSocketChannel.open();
	}

	public void start(int port) throws Exception {
		serverSocketChannel.bind(new InetSocketAddress(port));
		
		System.out.println("Server started");
		
		AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(serverSocketChannel);
		serverSocketChannel.accept(null, acceptCompletionHandler);
		
		System.in.read();
		System.out.println("Server done");
	}
	
	
}
