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
	private Semaphore decodeMutex, addMutex;
	
	public MessageQueue(AsynchronousSocketChannel socketChan) {
//		messageToSendQueue = new LinkedList<Attachment>();
		pendingWriteList = new ArrayList<Attachment>();
		pendingReadList = new ArrayList<Attachment>();
		messageDoneList = new ArrayList<Attachment>();
		this.socketChannel = socketChan;
		this.isSending = new AtomicBoolean(false);
		this.isReading = new AtomicBoolean(false);
		this.decodeMutex = new Semaphore(1);
		this.addMutex = new Semaphore(1);
	}
	
	public int pushMessageToSendQueue(String strMsg, int msgID) throws Exception {
		// create attachment
		Attachment attachment = new Attachment(strMsg, true);
		attachment.setAttachmentID(msgID);
		addMutex.acquire();
		this.pendingWriteList.add(attachment);
		addMutex.release();
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
		ByteBuffer buffer = ByteBuffer.wrap(attachment.getSendMessage().getBytes(StandardCharsets.UTF_8));
		isSending.set(true);
		Future<Integer> futureWrite = socketChannel.write(buffer);
		try {
			decodeMutex.acquire();
			String tmp = StandardCharsets.UTF_8.newDecoder().reset().decode(buffer).toString();
			System.out.println("Message to be sent: " + tmp);
			decodeMutex.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			futureWrite.get();
			pendingReadList.add(attachment);
			isSending.set(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void readMessage() {
		// Attachment tmpAttachment = new Attachment();
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		Future<Integer> futureRead = socketChannel.read(buffer);
		try {
			isReading.set(true);
			futureRead.get();
			mergeMessage(buffer);
			isReading.set(false);
		} catch (Exception e) {
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
			
			for(Attachment a : pendingReadList) {
				if(a.getAttachmentID() == msgID) {
					a.setReturnMessage(tmp);
					messageDoneList.add(a);
					pendingReadList.remove(a);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
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
							if(!isSending.get()) {
								Attachment atchmtToSend = pendingWriteList.remove(0);
								sendMessage(atchmtToSend);
							}
						} 
				}  catch (Exception e) {
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
						if(!isReading.get()) {
							readMessage();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	
			}
		}
		
	}
}
