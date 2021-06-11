package client.network;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import protocol.Attachment;

public class MessageQueue {
	private Queue<Attachment> messageToSendQueue;
	private LinkedList<Attachment> messageDoneList;
	private AsynchronousSocketChannel socketChannel;
	private AtomicBoolean isSending, isReading;
	private int counter; // for assigning attachmentID only;
	private Semaphore sendMutex, addMutex;
	
	public MessageQueue(AsynchronousSocketChannel socketChan) {
		messageToSendQueue = new LinkedList<Attachment>();
		messageDoneList = new LinkedList<Attachment>();
		this.socketChannel = socketChan;
		this.isSending = new AtomicBoolean(false);
		this.isReading = new AtomicBoolean(false);
		this.sendMutex = new Semaphore(1);
		this.addMutex = new Semaphore(1);
	}
	
	public int pushToMessageToSendQueue(String strMsg) throws Exception {
		// create attachment
		Attachment attachment = new Attachment(strMsg, true);
		attachment.setAttachmentID(counter);
		addMutex.acquire();
		this.messageToSendQueue.add(attachment);
		addMutex.release();
		System.out.println("Added: Size of msg queue is: " + messageToSendQueue.size());
		System.out.println("Attachment ID: " + attachment.getAttachmentID());
		counter++;
		return attachment.getAttachmentID();
	}
	
	public Attachment getAttachmentByID(int id) {
		for(Attachment a : messageDoneList) {
			if (a.getAttachmentID() == id) {
				return a;
			}
		}
		return null;
	}
	
	public void startMessageQueue() {
		Thread thread = new Thread(new MessageThread());
		thread.start();
	}
	
	private void sendMessage(Attachment attachment) {
//		Attachment attachment = new Attachment(strMsgToSend, true);
		ByteBuffer buffer = ByteBuffer.wrap(attachment.getSendMessage().getBytes(StandardCharsets.UTF_8));
		WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel, isSending);
		socketChannel.write(buffer, attachment, writeCompletionHandler);
//		isBusy.set(true);
		while (attachment.getActive().get()) {

		}
		System.out.println("This is printed from client: " + attachment.getReturnMessage());
//		return attachment.getReturnMessage();
		try {
			addMutex.acquire();
			messageDoneList.add(attachment);
			addMutex.release();
//			isBusy.set(false);
			System.out.println("Added to messageDoneList. Size: " + messageDoneList.size());
		} catch (Exception e) {
			System.out.println("sendMessage get error");
			e.printStackTrace();
		}
	}
	
	private class MessageThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
				Thread.sleep(500);
					if(messageToSendQueue.size()!=0) {
						System.out.println("Queue size is not 0. Start polling");
						if(!isBusy.get()) {
							System.out.println("Channel not busy. Start sending");							
								sendMutex.acquire();
								Attachment atchToSend = messageToSendQueue.poll();
								sendMessage(atchToSend);
								sendMutex.release();
						}
					} 
				} catch (Exception e) {
					System.out.println("Error in run of MessageThread");
					e.printStackTrace();
				}
	
			}
		}
		
	}
}
