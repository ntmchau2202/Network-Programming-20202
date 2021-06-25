package server.core.processor;

import protocol.Attachment;
import server.entity.network.completionHandler.WriteCompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

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
        // TODO: add mechanism for avoiding write pending case

        // TODO: check if isCancel -> change msg to cancel msg

        Attachment newAttachment = new Attachment(resMsg, true);
        ByteBuffer bufferRequest = ByteBuffer.wrap(resMsg.getBytes(StandardCharsets.UTF_8));

        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(this.socketChannel);
        this.socketChannel.write(bufferRequest, newAttachment, writeCompletionHandler);
        while (newAttachment.getActive().get()) {
            // waiting for the write complete
        }
        System.out.println("Done sending msg: " + resMsg);
        System.out.println("Is socket still open?: " + this.socketChannel.isOpen());
    }


}
