package server;

import server.core.ServerCore;

public class ServerMain {

	public static void main(String[] args) throws Exception{
		ServerCore server = new ServerCore();
		server.start(6666);
	}
}
