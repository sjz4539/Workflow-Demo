package controller.io;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import model.account.Account;
import model.resource.remote.RemoteFile;
import model.resource.remote.RemoteFolder;

public class GoogleFileOps extends FileOps{

	public FileOpsStatus loadFile(Account source, String remoteSource, String localDest){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus saveFile(Account source, String localSource, String remoteDest){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus copyFile(Account source, String remoteSource, String remoteDest){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus copyFile(Account source, RemoteFile remoteSource, String remoteDest){
		return copyFile(source, remoteSource.getPath(), remoteDest);
	}
	
	public FileOpsStatus moveFile(Account source, String remoteSource, String remoteDest){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus moveFile(Account source, RemoteFile remoteSource, String remoteDest){
		return moveFile(source, remoteSource.getPath(), remoteDest);
	}
	
	public FileOpsStatus deleteFile(Account source, String remoteSource){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus deleteFile(Account source, RemoteFile remoteSource){
		return deleteFile(source, remoteSource.getPath());
	}
	
	public FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource){
		return loadFolder(source, remoteSource, 1);
	}
	
	public FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource, int depth){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
			try {
				
				for(File file : drive.files().list().setQ(remoteSource.getId() + " in parents").execute().getItems()){
					
					if(file.getMimeType().equals("application/vnd.google-apps.folder")){
						remoteSource.addFolder(new RemoteFolder(file.getTitle(), remoteSource, source));
					}else{
						remoteSource.addFile(new RemoteFile(file.getTitle(), remoteSource, source));
					}
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus createFolder(Account source, RemoteFolder parent, String name){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus copyFolder(Account source, RemoteFolder remoteSource, String remoteDest){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus moveFolder(Account source, RemoteFolder remoteSource, String remoteDest){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	public FileOpsStatus deleteFolder(Account source, RemoteFolder remoteSource){
		GoogleCredential credential = getCredential(source);
		Drive drive = getDrive();
		
		if(credential != null && drive != null){
			
		}
		
		return FileOpsStatus.NOT_ALLOWED();
	}
	
	private Drive getDrive(){
		return new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null).build();
	}
	
	private GoogleCredential getCredential(Account source){
		String token = source.getAuth();
		
		if(token != null){
			return new GoogleCredential().setAccessToken(token);
		}else{
			return null;
		}
	}
	
}
