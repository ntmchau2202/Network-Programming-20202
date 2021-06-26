package client.network;

import client.utils.Configs;
import message.ServerMessage;
import message.chat.ChatClientMessage;
import message.chat.ChatServerMessage;
import message.chat.ListenChatClientMessage;
import message.chatack.ChatACKServerMessage;
import message.drawconfirm.DrawConfirmClientMessage;
import message.drawconfirm.DrawConfirmServerMessage;
import message.drawrequest.DrawRequestClientMessage;
import message.drawrequest.DrawRequestServerMessage;
import message.drawrequest.ListenDrawClientMessage;
import message.joinqueue.JoinQueueClientMessage;
import message.leaderboard.LeaderboardClientMessage;
import message.login.LoginClientMessage;
import message.logout.LogoutClientMessage;
import message.matchfound.MatchFoundServerMessage;
import message.move.ListenMoveClientMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import message.quitgame.QuitGameClientMessage;
import message.quitqueue.QuitQueueClientMessage;
import message.register.RegisterClientMessage;
import message.updateuser.UpdateUserClientMessage;
import protocol.Attachment;
import protocol.Command;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

import org.json.JSONObject;
import server.core.authentication.T3Authenticator;

public class ClientSocketChannel {
    private static ClientSocketChannel socketChannelInstance;
    private AsynchronousSocketChannel socketChannel;
    private MessageQueue messageQueue;
//	private static WriteCompletionHandler writeCompletionHandler;

    private Attachment attachment;
    // private ClientMessage clientMsg;
    private ByteBuffer buffer;

    // ================= INITIALIZER ======================

    private ClientSocketChannel() {

        // set option for socket channel
        try {
            socketChannel = AsynchronousSocketChannel.open();
            socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4096);
            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 4096);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // connect to server
        socketChannel.connect(new InetSocketAddress(Configs.IP_ADDRESS, Configs.PORT));

        // init write completion handler
//		writeCompletionHandler = new WriteCompletionHandler(socketChannel);

        // additional implementation for operations
//		 clientMsg = new ClientMessage();
        buffer = ByteBuffer.allocate(4096);
        messageQueue = new MessageQueue(socketChannel);
        messageQueue.startMessageQueue();
        // how to implement close socket when object is destroyed?
    }

    private static class ClientSocketChannelSingleton {
        private static final ClientSocketChannel INSTANCE = new ClientSocketChannel();
    }

    public static ClientSocketChannel getSocketInstance() {
        return ClientSocketChannel.ClientSocketChannelSingleton.INSTANCE;
    }
    
    public static boolean isConnected() {
    	if(socketChannelInstance!=null) {
        	if(socketChannelInstance.socketChannel != null && socketChannelInstance.socketChannel.isOpen()) {
        		return true;
        	} 
    	}
    	
    	return false;
    }
    
   public static void closeConnection() throws Exception {
	   socketChannelInstance.socketChannel.close();
    }

    // =================== FUNCTIONS =======================

    public Attachment sendRequestAsync(String strMsgToSend) throws Exception {
        attachment = new Attachment(strMsgToSend, true);
        buffer = ByteBuffer.wrap(strMsgToSend.getBytes(StandardCharsets.UTF_8));
        WriteAndReturnHandler writeCompletionHandler = new WriteAndReturnHandler(socketChannel);
        socketChannel.write(buffer, attachment, writeCompletionHandler);
        ByteBuffer readBuffer = ByteBuffer.allocate(4096);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(readBuffer);
        socketChannel.read(readBuffer, attachment, readCompletionHandler);
        return attachment;
    }

    public String receiveResponseAsync(Attachment att) throws Exception {
        while (attachment.getActive().get()) {

        }
        System.out.println("This is printed from client: " + attachment.getReturnMessage());
        return attachment.getReturnMessage();
    }

    private String sendRequest(String strMsgToSend, int msgCmdID) throws Exception {
//		System.out.println(this.getSocketInstance().socketChannel.toString());
//		attachment = new Attachment(strMsgToSend, true);
//		buffer = ByteBuffer.wrap(strMsgToSend.getBytes(StandardCharsets.UTF_8));
//		WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
//		socketChannel.write(buffer, attachment, writeCompletionHandler);
//		while (attachment.getActive().get()) {
//
//		}
//		System.out.println("This is printed from client: " + attachment.getReturnMessage());
//		return attachment.getReturnMessage();
        int msgID = messageQueue.pushMessageToSendQueue(strMsgToSend, msgCmdID);
        System.out.println("sendRequest: received msgID: " + msgID);
        while (true) {
            Thread.sleep(500);
            Attachment attachment = messageQueue.getAttachmentByID(msgID);
            if (attachment != null) {
                return attachment.getReturnMessage();
            }
        }
    }

    public String login(String username, String password) throws Exception {
        LoginClientMessage loginRequest = new LoginClientMessage(username, password);
        // return sendRequest(loginRequest.toString());
//		return sendRequest(loginRequest.toString());
        int msgID = messageQueue.pushMessageToSendQueue(loginRequest.toString(), loginRequest.getMessageCommandID());
        System.out.println("Returned msg ID: " + msgID);
        while (true) {
            Thread.sleep(500);
            Attachment attachment = messageQueue.getAttachmentByID(msgID);
            if (attachment != null) {
                return attachment.getReturnMessage();
            }
        }
//		return receiveResponseAsync(att);
    }

    public String register(String username, String password) throws Exception {
        RegisterClientMessage registerRequest = new RegisterClientMessage(username, password);
        return sendRequest(registerRequest.toString(), registerRequest.getMessageCommandID());
    }

    public String joinQueue(String sesID, String mode) throws Exception {
        JoinQueueClientMessage joinQueueRequest = new JoinQueueClientMessage(mode, sesID);
//		return sendRequestAsync(joinQueueRequest.toString());
        return sendRequest(joinQueueRequest.toString(), joinQueueRequest.getMessageCommandID());
    }

    public String quitQueue(String username, String sessionID) throws Exception {
        QuitQueueClientMessage quitQueueRequest = new QuitQueueClientMessage(username, sessionID);
//		return sendRequestAsync(quitQueueRequest.toString());
        return sendRequest(quitQueueRequest.toString(), quitQueueRequest.getMessageCommandID());
    }

	public String logout(String username, String sessionID) throws Exception {
		LogoutClientMessage logoutRequest = new LogoutClientMessage(username, sessionID);
		return sendRequest(logoutRequest.toString(), logoutRequest.getMessageCommandID());
	}

//	public String move(String player, String sesID, int matchID, int x, int y, String state, String result)
//			throws Exception {
//		MoveClientMessage msg = new MoveClientMessage(matchID, player, sesID, x, y, state, result);
//		return sendRequest(msg.toString(), msg.getMessageCommandID());
//	}

    public String move(String player, String sesID, int matchID, int x, int y, String state, String result)
            throws Exception {
        MoveClientMessage msg = new MoveClientMessage(matchID, player, sesID, x, y, state, result);
        return sendRequest(msg.toString(), msg.getMessageCommandID());
    }

    public String listenMove(String playerName, int matchID) throws Exception {
        ListenMoveClientMessage msg = new ListenMoveClientMessage(playerName, matchID);
        return sendRequest(msg.toString(), msg.getMessageCommandID());
    }

    public String listenChat(String playerName, int matchID) throws Exception {
        ListenChatClientMessage msg = new ListenChatClientMessage(playerName, matchID);
        return sendRequest(msg.toString(), msg.getMessageCommandID());
    }

	public String requestDraw(int matchID, String username, String sessionID) throws Exception {
		DrawRequestClientMessage msg = new DrawRequestClientMessage(matchID, username, sessionID);
		return sendRequest(msg.toString(), msg.getMessageCommandID());
	}
	
	public String quitGame(int matchID, String username, String sessionID) throws Exception {
		QuitGameClientMessage msg = new QuitGameClientMessage(matchID, username, sessionID);
		return sendRequest(msg.toString(), msg.getMessageCommandID());
	}

	public String confirmDraw(int matchID, String player, String sessionID, boolean acceptance) throws Exception {
		DrawConfirmClientMessage msg = new DrawConfirmClientMessage(matchID, player, sessionID, acceptance);
		return sendRequest(msg.toString(), msg.getMessageCommandID());
	}

	public String getLeaderBoard(String usr, String sesID) throws Exception {
		LeaderboardClientMessage msg = new LeaderboardClientMessage(usr, sesID);
		return sendRequest(msg.toString(), msg.getMessageCommandID());
	}

    public String chat(String fromUsr, String toUsr, String chatMsg, int matchID) throws Exception {
        ChatClientMessage msg = new ChatClientMessage(fromUsr, toUsr, chatMsg, matchID);
        return sendRequest(msg.toString(), msg.getMessageCommandID());
    }

    public String updateUser(String username, String sessionID) throws Exception {
        UpdateUserClientMessage msg = new UpdateUserClientMessage(username, sessionID);
        return sendRequest(msg.toString(), msg.getMessageCommandID());
    }

    public String listenDrawRequest(String username, int matchID) throws Exception {
    	ListenDrawClientMessage msg = new ListenDrawClientMessage(username, matchID);
    	return sendRequest(msg.toString(), msg.getMessageCommandID());
    }
}
