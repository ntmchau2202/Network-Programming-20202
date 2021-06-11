package protocol;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class Attachment {
	private final String sendMessage;
	private String []returnMessage;
	private final AtomicBoolean active;
	private AsynchronousSocketChannel socket;
	private int attachmentID;
	
	public Attachment() {
		this.sendMessage = "";
		this.active = new AtomicBoolean(true);
		
		// tricky, aren't they?
		this.returnMessage = new String[1];
		this.returnMessage[0] = "";
	}
	
	public int getAttachmentID() {
		return this.attachmentID;
	}
	
	public void setAttachmentID(int id) {
		this.attachmentID = id;
	}
	
	public Attachment(String msg, boolean act) {
		this.sendMessage = msg;
		this.active = new AtomicBoolean(act);
		
		this.returnMessage = new String[1];
		this.returnMessage[0] = "";
	}
	
	public String getSendMessage() {
		return this.sendMessage;
	}
	
	public String getReturnMessage() {
		return this.returnMessage[0];
	}
	
	public void setReturnMessage(String retMsg) {
		this.returnMessage[0] = retMsg;
	}
	
	public AtomicBoolean getActive() {
		return this.active;
	}
	
	public AsynchronousSocketChannel getSocket() {
		return this.socket;
	}
	
	public void setSocket(AsynchronousSocketChannel sock) {
		this.socket = sock;
	}
}

