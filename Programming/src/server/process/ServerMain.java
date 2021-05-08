package server.process;

public class ServerMain {

	public static void main(String[] args) throws Exception{
		Server server = new Server();
		server.start(6666);
	}
}
