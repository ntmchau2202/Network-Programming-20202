package protocol;

public enum StatusCode {
	SUCCESS("success"),
	ERROR("error");
	
	private String statusCode;
	
	StatusCode(String statCode){
		this.statusCode = statCode;
	}
	
	public String getStatusCodeString() {
		return statusCode;
	}
}
