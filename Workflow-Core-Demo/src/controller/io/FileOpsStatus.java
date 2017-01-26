package controller.io;

public class FileOpsStatus {

	public enum Code{
		SUCCESS, BAD_REQUEST, BAD_RESPONSE, INVALID_OAUTH_TOKEN, LOCAL_IO_ERROR, NETWORK_IO_ERROR, PROTOCOL_ERROR, SERVER_BUSY, SERVER_ERROR, 
		DROPBOX_NULL_CLIENT, FILE_NOT_FOUND, FILE_ALREADY_EXISTS, GENERIC_DROPBOX_ERROR, GENERIC_GOOGLE_ERROR, NOT_ALLOWED
	}
	
	private Code code = Code.SUCCESS;
	private String message = "";
	
	public FileOpsStatus(){
		this(Code.SUCCESS, null);
	}
	
	public FileOpsStatus(Code c, String m){
		code = c;
		message = (m != null ? m : "");
	}
	
	public Code getCode(){
		return code;
	}
	
	public String getMessage(){
		return message;
	}
	
//=======================================================
	
	public static FileOpsStatus SUCCESS(){
		return new FileOpsStatus();
	}
	
	public static FileOpsStatus BAD_REQUEST(){
		return new FileOpsStatus(Code.BAD_REQUEST, "The submitted request was malformed.");
	}
	
	public static FileOpsStatus BAD_RESPONSE(){
		return new FileOpsStatus(Code.BAD_RESPONSE, "The response received was malformed.");
	}
	
	public static FileOpsStatus INVALID_OAUTH_TOKEN(){
		return new FileOpsStatus(Code.INVALID_OAUTH_TOKEN, "The stored authorization tokens for this account are invalid.");
	}
	
	public static FileOpsStatus LOCAL_IO_ERROR(){
		return new FileOpsStatus(Code.LOCAL_IO_ERROR, "A local I/O error occurred.");
	}
	
	public static FileOpsStatus NETWORK_IO_ERROR(){
		return new FileOpsStatus(Code.NETWORK_IO_ERROR, "A network I/O error occurred.");
	}
	
	public static FileOpsStatus PROTOCOL_ERROR(){
		return new FileOpsStatus(Code.PROTOCOL_ERROR, "A network protocol error occurred.");
	}
	
	public static FileOpsStatus SERVER_BUSY(){
		return new FileOpsStatus(Code.SERVER_BUSY, "The remote server is busy.");
	}

	public static FileOpsStatus SERVER_ERROR(){
		return new FileOpsStatus(Code.SERVER_ERROR, "The remote server encountered an error.");
	}
	
	public static FileOpsStatus DROPBOX_NULL_CLIENT(){
		return new FileOpsStatus(Code.DROPBOX_NULL_CLIENT, "The Dropbox API returned a null client object.");
	}
	
	public static FileOpsStatus FILE_ALREADY_EXISTS(){
		return new FileOpsStatus(Code.FILE_ALREADY_EXISTS, "A file or folder already exists at that location and could not be overwritten.");
	}
	
	public static FileOpsStatus FILE_NOT_FOUND(){
		return new FileOpsStatus(Code.FILE_NOT_FOUND, "The specified file or folder could not be found.");
	}
	
	public static FileOpsStatus GENERIC_DROPBOX_ERROR(){
		return new FileOpsStatus(Code.GENERIC_DROPBOX_ERROR, "Dropbox reported a nonspecific error.");
	}
	
	public static FileOpsStatus GENERIC_GOOGLE_ERROR(){
		return new FileOpsStatus(Code.GENERIC_GOOGLE_ERROR, "Google reported a nonspecific error.");
	}
	
	public static FileOpsStatus NOT_ALLOWED(){
		return new FileOpsStatus(Code.NOT_ALLOWED, "This operation is not allowed for this storage location.");
	}
	
}
