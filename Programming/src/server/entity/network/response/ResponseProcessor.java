package server.entity.network.response;

import protocol.Attachment;
import server.entity.network.completionHandler.WriteCompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ResponseProcessor {
    private void sendResponse(Map<AsynchronousSocketChannel, String> listMsg) {

        for (Map.Entry<AsynchronousSocketChannel, String> item : listMsg.entrySet()) {
            String msg = item.getValue();
            AsynchronousSocketChannel toSocket = item.getKey();
            Attachment newAttachment = new Attachment(msg, true);
            ByteBuffer bufferRequest = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));

            WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(toSocket);
            toSocket.write(bufferRequest, newAttachment, writeCompletionHandler);
            while (newAttachment.getActive().get()) {

            }
            completionHandlerController.removeHandlerFromList(this);
            System.out.println("Done sending msg: " + msg);
            System.out.println("Is socket still open?: " + toSocket.isOpen());
        }
    }


}
