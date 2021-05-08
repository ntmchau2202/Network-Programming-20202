package client.process;

public class ClientMain {
	
	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.start("localhost", 6666);
	}
}
