package client.network;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import protocol.Attachment;

public class MessageQueue {
//	private Queue<Attachment> messageToSendQueue;
	private ArrayList<Attachment> pendingWriteList, pendingReadList, messageDoneList;
	private AsynchronousSocketChannel socketChannel;
	private AtomicBoolean isSending, isReading;
	private Semaphore sendMutex, addMutex;
	
	public MessageQueue(AsynchronousSocketChannel socketChan) {
//		messageToSendQueue = new LinkedList<Attachment>();
		pendingWriteList = new ArrayList<Attachment>();
		pendingReadList = new ArrayList<Attachment>();
		messageDoneList = new ArrayList<Attachment>();
		this.socketChannel = socketChan;
		this.isSending = new AtomicBoolean(false);
		this.isReading = new AtomicBoolean(false);
		this.sendMutex = new Semaphore(1);
		this.addMutex = new Semaphore(1);
	}
	
	public int pushMessageToSendQueue(String strMsg, int msgID) throws Exception {
		// create attachment
		Attachment attachment = new Attachment(strMsg, true);
		attachment.setAttachmentID(msgID);
		addMutex.acquire();
		this.pendingWriteList.add(attachment);
		addMutex.release();
		System.out.println("Added: Size of msg queue is: " + pendingWriteList.size());
		System.out.println("Attachment ID: " + attachment.getAttachmentID());
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
		Thread readThread = new Thread(new MessageSendThread());
		Thread writeThread = new Thread(new MessageReadThread());
		readThread.start();
		writeThread.start();
	}
		
	private void sendMessage(Attachment attachment) {
//		Attachment attachment = new Attachment(strMsgToSend, true);
		ByteBuffer buffer = ByteBuffer.wrap(attachment.getSendMessage().getBytes(StandardCharsets.UTF_8));
//		WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
		isSending.set(true);
//		socketChannel.write(buffer, attachment, writeCompletionHandler);
		Future<Integer> futureWrite = socketChannel.write(buffer);
		try {
			String tmp = null;
			tmp = StandardCharsets.UTF_8.newDecoder().reset().decode(buffer).toString();
			System.out.println("Written message: " + tmp);
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		System.out.println("write returned");
		try{
			futureWrite.get();
			pendingReadList.add(attachment);
			isSending.set(false);
		}catch (Exception e) {
			System.out.println("Error when sending message: " + attachment.getSendMessage());
			e.printStackTrace();
		}
		
		
////		isBusy.set(true);
//		while (attachment.getActive().get()) {
//
//		}
//		System.out.println("This is printed from client: " + attachment.getReturnMessage());
////		return attachment.getReturnMessage();
//		try {
//			addMutex.acquire();
//			messageDoneList.add(attachment);
//			addMutex.release();
////			isBusy.set(false);
//			System.out.println("Added to messageDoneList. Size: " + messageDoneList.size());
//		} catch (Exception e) {
//			System.out.println("sendMessage get error");
//			e.printStackTrace();
//		}
	}
	
	private void readMessage() {
		// Attachment tmpAttachment = new Attachment();
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		Future<Integer> futureRead = socketChannel.read(buffer);
		System.out.println("read returned");
		try {
			isReading.set(true);
			futureRead.get();
			mergeMessage(buffer);
			isReading.set(false);
		} catch (Exception e) {
			System.out.println("Error when reading...");
			e.printStackTrace();
		}
	}
	
	private boolean mergeMessage(ByteBuffer inputBuffer) {
		try {
			inputBuffer.flip();
			String tmp = StandardCharsets.UTF_8.newDecoder().decode(inputBuffer).toString();
			System.out.println("Msg received: " + tmp);
			JSONObject tmpJS = new JSONObject(tmp);
			int msgID = tmpJS.getInt("message_id");
			System.out.println("Msg ID received from server: " + msgID);
			
			for(Attachment a : pendingReadList) {
				System.out.println("item: " + a.getAttachmentID());
				if(a.getAttachmentID() == msgID) {
					a.setReturnMessage(tmp);
					messageDoneList.add(a);
					pendingReadList.remove(a);
					System.out.println("Size of pendingReadList: " + pendingReadList.size());
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			System.out.println("Error when merging message");
			e.printStackTrace();
			return false;
		}
	}
	
	private class MessageSendThread implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(500);
						// send
						if(pendingWriteList.size()!=0) {
							System.out.println("Pending send queue size is not 0. Start polling");
							if(!isSending.get()) {
								System.out.println("Channel not busy. Start sending");
								Attachment atchmtToSend = pendingWriteList.remove(0);
								sendMessage(atchmtToSend);
							}
						} 
				}  catch (Exception e) {
					System.out.println("Error in run of MessageThread");
					e.printStackTrace();
				}
			}
		}
	}
	
	private class MessageReadThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
				Thread.sleep(500);
					if(pendingReadList.size()!=0) {
						System.out.println("Pending read queue is not 0. Start reading and merging");
						if(!isReading.get()) {
							System.out.println("Channel not busy. Start receiving");
							readMessage();
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
