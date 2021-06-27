package server.entity.DrawRecord;

public class DrawRecord {
	private String requester;
	private boolean isAccepted;
	private boolean isSeenByBoth;
	private int matchID;
	
	public DrawRecord(String requester, int matchID) {
		this.requester = requester;
		this.matchID = matchID;
	}
	
	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}
	
	public void setSeenByBoth(boolean seen) {
		this.isSeenByBoth = seen;
	}
	
	public boolean isSeenByBoth() {
		return this.isSeenByBoth;
	}
	
	public String getRequester() {
		return this.requester;
	}
	
	public boolean getAcceptance() {
		return this.isAccepted;
	}
}
