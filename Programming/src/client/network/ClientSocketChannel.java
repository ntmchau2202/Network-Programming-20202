package client.network;

import client.utils.Configs;
import message.login.LoginClientMessage;
import message.register.RegisterClientMessage;
import protocol.Attachment;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientSocketChannel {
    private static ClientSocketChannel socketChannelInstance;
    private static AsynchronousSocketChannel socketChannel;
    private static WriteCompletionHandler writeCompletionHandler;
    
    
    private Attachment attachment;
    // private ClientMessage clientMsg;
    private ByteBuffer buffer;
    
    // ================= INITIALIZER ======================

    private ClientSocketChannel() throws IOException {
        socketChannel = AsynchronousSocketChannel.open();

        // set option for socket channel
        socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4096);
        socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 4096);
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        // connect to server
        socketChannel.connect(new InetSocketAddress(Configs.IP_ADDRESS, Configs.PORT));

        // init write completion handler
        writeCompletionHandler = new WriteCompletionHandler(socketChannel);
        
        // additional implementation for operations
        // clientMsg = new ClientMessage();
        buffer = ByteBuffer.allocate(4096);
        

        // how to implement close socket when object is destroyed?
    }

    public static ClientSocketChannel getSocketInstance() throws IOException {
        if (socketChannelInstance == null) socketChannelInstance = new ClientSocketChannel();
        return socketChannelInstance;
    }
    
    // =================== FUNCTIONS =======================
    
    private String sendRequest(String strMsgToSend) throws Exception {
		attachment = new Attachment(strMsgToSend, true);
		buffer = ByteBuffer.wrap(strMsgToSend.getBytes(StandardCharsets.UTF_8));
		socketChannel.write(buffer, attachment, writeCompletionHandler);
		while (attachment.getActive().get()) {
			
		}
		System.out.println("This is printed from client: " + attachment.getReturnMessage());
		return attachment.getReturnMessage();
		
	}
    
    public String login(String username, String password) throws Exception {
//		clientMsg.createLoginRequest(username, password);
    	LoginClientMessage loginRequest = new LoginClientMessage(username, password);
		return sendRequest(loginRequest.toString());
	}
	
	public String register(String username, String password) throws Exception {
		RegisterClientMessage registerRequest = new RegisterClientMessage(username, password);
		return sendRequest(registerRequest.toString());
	}
	
	public String joinQueue(String mode) throws Exception {
		// TODO: Finish function
		return sendRequest("");
	}
	
	public String move(int x, int y, String state, String result) throws Exception {
		// TODO: Finish function
		return sendRequest("");
	}
	
	public String requestDraw() throws Exception {
		// TODO: Finish function
		return sendRequest("");
	}
	
	public String confirmDraw() throws Exception {
		// TODO: Finish function
		return sendRequest("");
	}
	
	public String getLeaderBoard() throws Exception {
		// TODO: Finish function
		return sendRequest("");
	}
	
	public String chat(String chatMsg) throws Exception {
		// TODO: Finish function
		return sendRequest("");
	}
	
	public String chatACK() throws Exception {
		// TODO: Finish function
		return sendRequest("");
	}
	
	public String logout() throws Exception {
		// TODO: Finish function
		return sendRequest("");
	}
    
}
