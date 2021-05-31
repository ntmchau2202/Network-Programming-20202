package client.network;

import client.utils.Configs;
import javafx.application.Platform;
import message.ServerMessage;
import message.chat.ChatServerMessage;
import message.chatack.ChatACKServerMessage;
import message.drawconfirm.DrawConfirmServerMessage;
import message.drawrequest.DrawRequestServerMessage;
import message.joinqueue.JoinQueueClientMessage;
import message.login.LoginClientMessage;
import message.matchfound.MatchFoundServerMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import message.register.RegisterClientMessage;
import protocol.Attachment;
import protocol.Command;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONObject;

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
		if (socketChannelInstance == null)
			socketChannelInstance = new ClientSocketChannel();
		return socketChannelInstance;
	}

	// =================== FUNCTIONS =======================

	public Attachment sendRequestAsync(String strMsgToSend) throws Exception {
		attachment = new Attachment(strMsgToSend, true);
		buffer = ByteBuffer.wrap(strMsgToSend.getBytes(StandardCharsets.UTF_8));
		socketChannel.write(buffer, attachment, writeCompletionHandler);
		return attachment;
	}

	public String receiveResponseAsync(Attachment att) throws Exception {
		while (attachment.getActive().get()) {

		}
		System.out.println("This is printed from client: " + attachment.getReturnMessage());
		return attachment.getReturnMessage();
	}

	private String sendRequest(String strMsgToSend) throws Exception {
		System.out.println(this.getSocketInstance().socketChannel.toString());
		attachment = new Attachment(strMsgToSend, true);
		buffer = ByteBuffer.wrap(strMsgToSend.getBytes(StandardCharsets.UTF_8));
		socketChannel.write(buffer, attachment, writeCompletionHandler);
		while (attachment.getActive().get()) {

		}
		System.out.println("This is printed from client: " + attachment.getReturnMessage());
		return attachment.getReturnMessage();

	}

	public String login(String username, String password) throws Exception {
		LoginClientMessage loginRequest = new LoginClientMessage(username, password);
		// return sendRequest(loginRequest.toString());
		Attachment att = sendRequestAsync(loginRequest.toString());
		return receiveResponseAsync(att);
	}

	public String register(String username, String password) throws Exception {
		RegisterClientMessage registerRequest = new RegisterClientMessage(username, password);
		return sendRequest(registerRequest.toString());
	}

	public String joinQueue(String sesID, String mode) throws Exception {
		JoinQueueClientMessage joinQueueRequest = new JoinQueueClientMessage(mode, sesID);
		return sendRequest(joinQueueRequest.toString());
	}

	public String move(String player, String sesID, int matchID, int x, int y, String state, String result)
			throws Exception {
		MoveClientMessage msg = new MoveClientMessage(matchID, player, sesID, x, y, state, result);
		System.out.println(msg.toString());
		return sendRequest(msg.toString());
	}

	public String listenMove() {
		return "";
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

	public ServerMessage listenIngameMessage() {

		ByteBuffer inputBuffer = ByteBuffer.allocate(4096);
		String returnMsg = "";

		Future<Integer> result = this.socketChannel.read(inputBuffer);
		try {
			result.get();
			inputBuffer.flip();
			returnMsg = StandardCharsets.UTF_8.newDecoder().decode(inputBuffer).toString();
			System.out.println("This is printed from client: " + returnMsg);
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		// analyze things here?
		
		JSONObject jsMsg = new JSONObject(returnMsg);
		Command cmd = Command.toCommand(jsMsg.getString("command_code"));
		
		switch(cmd) {
		case MATCH_FOUND:{
			MatchFoundServerMessage matchFoundMsg = new MatchFoundServerMessage(returnMsg);
			return matchFoundMsg;
		}
		case MOVE:{
			MoveServerMessage moveMsg = new MoveServerMessage(returnMsg);
			return moveMsg;			
		}
		case CHAT:{
			ChatServerMessage chatMsg = new ChatServerMessage(returnMsg);
			return chatMsg;
			
		}
		case CHATACK:{
			ChatACKServerMessage ack = new ChatACKServerMessage(returnMsg);
			return ack;		
		}
		
		case DRAW_REQUEST: {
			DrawRequestServerMessage drawRequest = new DrawRequestServerMessage(returnMsg);
			return drawRequest;
		}
		case DRAW_CONFIRM:{
			DrawConfirmServerMessage drawConfirm = new DrawConfirmServerMessage(returnMsg);
			return drawConfirm;
			
		}
		}
		
		return null;
	}
}
