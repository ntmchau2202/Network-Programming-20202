package AsynchronousServer;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class AsynchronousServer {
	public static void main(String[] args) throws Exception {
		AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(6666));
		
		System.out.println("Server started");
		
		AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(serverSocketChannel);
		serverSocketChannel.accept(null, acceptCompletionHandler);
		
		System.in.read();
		System.out.println("Server done");
	}
}
