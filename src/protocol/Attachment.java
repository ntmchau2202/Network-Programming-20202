package protocol;

import java.util.concurrent.atomic.AtomicBoolean;

public class Attachment {
	private final String message;
	private final AtomicBoolean active;
	
	public Attachment(String msg, boolean act) {
		this.message = msg;
		this.active = new AtomicBoolean(act);
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public AtomicBoolean getActive() {
		return this.active;
	}
}
