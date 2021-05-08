package client.network;

import client.utils.Configs;
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

        // how to implement close socket when object is destroyed?
    }

    public static ClientSocketChannel getSocketInstance() throws IOException {
        if (socketChannelInstance == null) socketChannelInstance = new ClientSocketChannel();
        return socketChannelInstance;
    }

    public void sendMessageToServer(String msgToSend) {
        ByteBuffer buffer;
        Attachment attachment = new Attachment(msgToSend, true);
        buffer = ByteBuffer.wrap(msgToSend.getBytes(StandardCharsets.UTF_8));
        socketChannel.write(buffer, attachment, writeCompletionHandler);
    }
}
