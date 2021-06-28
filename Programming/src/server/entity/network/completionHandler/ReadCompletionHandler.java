package server.entity.network.completionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import protocol.Attachment;
import protocol.Command;
import server.core.ServerCore;
import server.core.controller.CompletionHandlerController;
import server.core.controller.QueueController;
import server.core.logger.T3Logger;
import server.core.processor.RequestProcessor;
import server.core.processor.ResponseProcessor;
import server.core.utils.Misc;


public class ReadCompletionHandler implements CompletionHandler<Integer, Attachment>{
	public static Logger LOGGER = T3Logger.getLogger(ReadCompletionHandler.class.getName());

	private final ByteBuffer buffer;
	private final CompletionHandlerController completionHandlerController;
	private final RequestProcessor reqProc;
	private final ResponseProcessor resProc;
	private Command cmd;
	private final String handlerID;
	private boolean isStop;

	public ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer buffer, QueueController queueController, CompletionHandlerController completionHandlerController) {
		this.buffer = buffer;
		this.completionHandlerController = completionHandlerController;
		// init processors
		this.reqProc = new RequestProcessor(queueController, completionHandlerController);
		this.resProc = new ResponseProcessor(socketChannel);
		this.handlerID = this.completionHandlerController.getHandlerCtrlID() + ">>>" + Misc.genShortUUID();
		this.isStop = false;
	}
	
	public void cancelHandler() {
		LOGGER.info("Canceling handler " + this.handlerID);
		this.reqProc.cancelProcessingRequest();
		this.resProc.cancelProcessingRequest();
	}

	public void forceStopHandler() {
		if (!isStop) {
			// stop all processors
			LOGGER.info("Force stopping handler " + this.handlerID);
			this.reqProc.stopAll();
			this.resProc.stopAll();
			this.isStop = true;
		}
	}

	public String getHandlerID() {
		return this.handlerID;
	}

	public Command getCommand() {
		return this.cmd;
	}
	
	@Override
	public void completed(Integer result, Attachment attachment) {
		// log out what received
		System.out.println("Bytes received: " + result.toString() + " in handler " + this.handlerID);

		// parsing data
		if (result > 0) {
		
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);

			String recvMsg = new String(bytes, StandardCharsets.UTF_8);
			System.out.println("Message received: " + recvMsg);

			attachment.setReturnMessage(recvMsg);
//			this.setCommand(recvMsg);

			// free attachment and allow new message from current client
			attachment.getActive().set(false);
			
			try {
				// store current message to Request Processor
				reqProc.storeCurrentCommand(recvMsg);

				// store current cmd to current handler
				this.cmd = reqProc.getCommand();
				
				// This is dangerous. What if there is other low priority command but are not required to be cancelled?
				
//				// trigger CompletionHandlerController to cancel other lower priority handler
//				if (this.completionHandlerController.cancelLowPriorityHandler(this.cmd)) {
//					 System.out.println("READCOMPLETIONHANDLER: Cancel other low priority operations successfully");
//				} else {
//					// TODO: print something here
//					System.out.println("READCOMPLETIONHANDLER: Cancel other low priority operations errorrrrrrrr");
//				}

				// process request message
				String msgToSend = reqProc.processReturn(recvMsg);

				// response client
				resProc.sendResponse(msgToSend);

				// remove current handler from handler list
				completionHandlerController.removeHandlerFromList(this);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (result == 0) {
			System.out.println("Client send empty message");
		} else {
			System.out.println("Connection closed on the client side");
		}

	}

	@Override
	public void failed(Throwable exc, Attachment attachment) {
		// this is when connection is shutdown expectedly
		LOGGER.info("***Handler " + this.handlerID + " shutdowns incorrectly***");
		// summon the ctrl to shutdown all handlers
		this.completionHandlerController.forceStopAllHandlers();

//		exc.printStackTrace();
	}
	
	public CompletionHandlerController getHandlerController() {
		return this.completionHandlerController;
	}
}
