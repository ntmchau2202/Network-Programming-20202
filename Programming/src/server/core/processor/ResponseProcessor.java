package server.core.processor;

import protocol.Attachment;
import server.core.logger.T3Logger;
import server.entity.network.IProcessor;
import server.entity.network.completionHandler.ReadCompletionHandler;
import server.entity.network.completionHandler.WriteCompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ResponseProcessor implements IProcessor {
    public static Logger LOGGER = T3Logger.getLogger(ResponseProcessor.class.getName());

    private final AsynchronousSocketChannel socketChannel;
    private boolean isCancel;
    private boolean isStop;

    public ResponseProcessor(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void cancelProcessingRequest() {
        this.isCancel = true;
    }

    @Override
    public void stopAll() {
        this.isStop = true;
    }

    public void sendResponse(String resMsg) {
        // TODO: add mechanism for avoiding write pending case

        if (!isStop) {
            // TODO: check if isCancel -> change msg to cancel msg

            Attachment newAttachment = new Attachment(resMsg, true);
            ByteBuffer bufferRequest = ByteBuffer.wrap(resMsg.getBytes(StandardCharsets.UTF_8));

            WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(this.socketChannel);
            this.socketChannel.write(bufferRequest, newAttachment, writeCompletionHandler);
            while (newAttachment.getActive().get()) {
                // waiting for the write complete
            }
            LOGGER.info("Done sending msg: " + resMsg);
//            System.out.println("Is socket still open?: " + this.socketChannel.isOpen());
        } else {
            LOGGER.info("Connection to client has been shutdown " + resMsg);
        }

    }


}
