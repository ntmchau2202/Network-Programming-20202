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
	
	public static StatusCode toStatusCode(String inputString) {
		for (StatusCode status : StatusCode.values()) {
			if (status.getStatusCodeString().compareToIgnoreCase(inputString) == 0){
				return status;
			}
		}
		return null;
	}
}
