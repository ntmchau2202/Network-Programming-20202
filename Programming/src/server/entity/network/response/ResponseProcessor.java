package server.entity.network.response;

import protocol.Attachment;
import server.entity.network.completionHandler.WriteCompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ResponseProcessor {
    private final AsynchronousSocketChannel socketChannel;
    private boolean isCancel;

    public ResponseProcessor(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void stopProcessingRequest() {
        this.isCancel = true;
    }

    public void sendResponse(String resMsg) {
        // TODO: check if isCancel -> change msg to cancel msg

        Attachment newAttachment = new Attachment(resMsg, true);
        ByteBuffer bufferRequest = ByteBuffer.wrap(resMsg.getBytes(StandardCharsets.UTF_8));

        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(this.socketChannel);
        this.socketChannel.write(bufferRequest, newAttachment, writeCompletionHandler);
        while (newAttachment.getActive().get()) {

        }
        System.out.println("Done sending msg: " + resMsg);
        System.out.println("Is socket still open?: " + this.socketChannel.isOpen());
    }


}
