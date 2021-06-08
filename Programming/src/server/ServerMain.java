package server;

import server.process.ServerSocketChannel;

public class ServerMain {

	public static void main(String[] args) throws Exception{
		ServerSocketChannel server = new ServerSocketChannel();
		server.start(6666);
	}
}
