package client.process;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

import client.network.WriteCompletionHandler;
import message.login.LoginClientMessage;
import protocol.Attachment;

public class Client {
	private BufferedReader stdIn;
	private ByteBuffer buffer;

	
	private AsynchronousSocketChannel clientSocketChannel;
	WriteCompletionHandler writeCompletionHandler;

	public Client() throws Exception {
		// setting up socket
		clientSocketChannel = AsynchronousSocketChannel.open();
		clientSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4096);
		clientSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 4096);
		clientSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		
		buffer = ByteBuffer.allocate(4086);
		writeCompletionHandler = new WriteCompletionHandler(clientSocketChannel);
		
		// this is for testing purpose only
		stdIn = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void start(String ip, int port) throws Exception {
		clientSocketChannel.connect(new InetSocketAddress(ip, port));
		String inputMessage;
		while ((inputMessage = stdIn.readLine())!=null) {
			switch (inputMessage) {
				case "login": {
					this.login("hikaru", "123456");
					break;
				}
				case "register": {
					this.register("hikaru", "akwshgabk");
					break;
				}
				case "join_queue": {
					joinQueue("normal");
					break;
				}
				case "move": {
					move(1, 2, "valid", "");
					break;
				}
				case "draw_request": {
					requestDraw();
					break;
				}
				case "draw_confirm": {
					confirmDraw();
					break;
				}
				case "leaderboard": {
					getLeaderBoard();
					break;
				}
				case "chat": {
					chat("hello");
					break;
				}
				case "chatACK": {
					chatACK();
					break;
				}
				case "logout": {
					logout();
					break;
				}
				case "exit": {
					stop();
					System.exit(0);
				}
				default: {
					System.out.println("unknown command");
				}
			}


		}

	}
	
	public void stop() throws Exception {
		clientSocketChannel.close();
		System.out.println("Client done");
	}
	
	// ================ FUNCTIONS ================
	
	private void sendRequest(String strMsgToSend) throws Exception {
		Attachment attachment = new Attachment(strMsgToSend, true);
		buffer = ByteBuffer.wrap(strMsgToSend.getBytes(StandardCharsets.UTF_8));
		clientSocketChannel.write(buffer, attachment, writeCompletionHandler);
		
	}
	
	public void login(String username, String password) throws Exception {
		LoginClientMessage clientMsg = new LoginClientMessage(username, password);
		sendRequest(clientMsg.toString());
	}
	
	public void register(String username, String password) throws Exception {
		// TODO: Finish function
	}
	
	public void joinQueue(String mode) {
		// TODO: Finish function
	}
	
	public void move(int x, int y, String state, String result) {
		// TODO: Finish function
	}
	
	public void requestDraw() {
		// TODO: Finish function
	}
	
	public void confirmDraw() {
		// TODO: Finish function
	}
	
	public void getLeaderBoard() {
		// TODO: Finish function
	}
	
	public void chat(String chatMsg) {
		// TODO: Finish function
	}
	
	public void chatACK() {
		// TODO: Finish function
	}
	
	public void logout() {
		// TODO: Finish function
	}
	
	
}
