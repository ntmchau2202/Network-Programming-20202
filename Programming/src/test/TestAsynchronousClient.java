package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

import client.network.WriteCompletionHandler;
import protocol.Attachment;

public class TestAsynchronousClient {	
	public static void main(String[] args) throws Exception {
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String inputMessage;
		ByteBuffer buffer;
		
		AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
		
		socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4096);
		socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 4096);
		socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		
		socketChannel.connect(new InetSocketAddress("localhost", 6666));
		WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
		
//		while ((inputMessage = stdIn.readLine())!=null) {
//			if (inputMessage.compareTo("c") != 0)
//				continue;
//			ClientMessage loginRequest = new ClientMessage();
//			loginRequest.createLoginRequest("hikaru", "abcde");
//			String msgToSend = loginRequest.toString();
//			
//			Attachment attachment = new Attachment(msgToSend, true);
//			buffer = ByteBuffer.wrap(msgToSend.getBytes(StandardCharsets.UTF_8));
//			socketChannel.write(buffer, attachment, writeCompletionHandler);
//			Attachment attachment = new Attachment(inputMessage, true);
//			buffer = ByteBuffer.wrap(inputMessage.getBytes(StandardCharsets.UTF_8));
//			socketChannel.write(buffer, attachment, writeCompletionHandler);
//		}
		

		
		socketChannel.close();
		System.out.println("Client done");
	}
}
