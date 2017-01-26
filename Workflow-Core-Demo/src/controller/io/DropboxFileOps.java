package controller.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

import model.account.Account;
import model.resource.remote.RemoteFile;
import model.resource.remote.RemoteFolder;

/**
 * Handles file operations via the Dropbox Java API.
 * 
 * @author Test
 *
 */
public class DropboxFileOps extends FileOps{
	
	public FileOpsStatus loadFile(Account source, String remoteSource, String localDest){	
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				
				File targetFile = new File(localDest);
				FileOutputStream out = new FileOutputStream(targetFile);
				remoteSource = remoteSource.replace('\\', '/');
				
				try{
					if(fileExists(source, remoteSource)){
						client.getFile(remoteSource, null, out);
					}else{
						result = FileOpsStatus.FILE_NOT_FOUND();
					}
				} catch (DbxException.BadRequest e) {
					e.printStackTrace();
					result = FileOpsStatus.BAD_REQUEST();
				} catch (DbxException.BadResponse e) {
					e.printStackTrace();
					result = FileOpsStatus.BAD_RESPONSE();
				} catch (DbxException.InvalidAccessToken e) {
					e.printStackTrace();
					result = FileOpsStatus.INVALID_OAUTH_TOKEN();
				} catch (DbxException.NetworkIO e) {
					e.printStackTrace();
					result = FileOpsStatus.NETWORK_IO_ERROR();
				} catch (DbxException.ProtocolError e) {
					e.printStackTrace();
					result = FileOpsStatus.PROTOCOL_ERROR();
				} catch (DbxException.RetryLater e) {
					e.printStackTrace();
					result = FileOpsStatus.SERVER_BUSY();
				} catch (DbxException.ServerError e) {
					e.printStackTrace();
					result = FileOpsStatus.SERVER_ERROR();
				} catch (DbxException e) {
					e.printStackTrace();
					result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
				} finally {
					out.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				result = FileOpsStatus.LOCAL_IO_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	public FileOpsStatus saveFile(Account source, String localSource, String remoteDest){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				File sourceFile = new File(localSource);
				FileInputStream in = new FileInputStream(sourceFile);
				
				try{
					client.uploadFile(remoteDest.replace('\\', '/'), DbxWriteMode.force(), -1, in);
				} catch (DbxException.BadRequest e) {
					e.printStackTrace();
					result = FileOpsStatus.BAD_REQUEST();
				} catch (DbxException.BadResponse e) {
					e.printStackTrace();
					result = FileOpsStatus.BAD_RESPONSE();
				} catch (DbxException.InvalidAccessToken e) {
					e.printStackTrace();
					result = FileOpsStatus.INVALID_OAUTH_TOKEN();
				} catch (DbxException.NetworkIO e) {
					e.printStackTrace();
					result = FileOpsStatus.NETWORK_IO_ERROR();
				} catch (DbxException.ProtocolError e) {
					e.printStackTrace();
					result = FileOpsStatus.PROTOCOL_ERROR();
				} catch (DbxException.RetryLater e) {
					e.printStackTrace();
					result = FileOpsStatus.SERVER_BUSY();
				} catch (DbxException.ServerError e) {
					e.printStackTrace();
					result = FileOpsStatus.SERVER_ERROR();
				} catch (DbxException e) {
					e.printStackTrace();
					result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
				} finally {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				result = FileOpsStatus.LOCAL_IO_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		
		return result;
	}
	
	public FileOpsStatus copyFile(Account source, String remoteSource, String remoteDest){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				if(fileExists(source, remoteSource)){
					client.copy(remoteSource.replace('\\', '/'), remoteDest.replace('\\', '/'));
				}else{
					result = FileOpsStatus.FILE_NOT_FOUND();
				}
			} catch (DbxException.BadRequest e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_REQUEST();
			} catch (DbxException.BadResponse e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_RESPONSE();
			} catch (DbxException.InvalidAccessToken e) {
				e.printStackTrace();
				result = FileOpsStatus.INVALID_OAUTH_TOKEN();
			} catch (DbxException.NetworkIO e) {
				e.printStackTrace();
				result = FileOpsStatus.NETWORK_IO_ERROR();
			} catch (DbxException.ProtocolError e) {
				e.printStackTrace();
				result = FileOpsStatus.PROTOCOL_ERROR();
			} catch (DbxException.RetryLater e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_BUSY();
			} catch (DbxException.ServerError e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_ERROR();
			} catch (DbxException e) {
				e.printStackTrace();
				result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	public FileOpsStatus copyFile(Account source, RemoteFile remoteSource, String remoteDest){
		return copyFile(source, remoteSource.getPath(), remoteDest);
	}
	
	public FileOpsStatus moveFile(Account source, String remoteSource, String remoteDest){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				if(fileExists(source, remoteSource)){
					client.move(remoteSource.replace('\\', '/'), remoteDest.replace('\\', '/'));
				}else{
					result = FileOpsStatus.FILE_NOT_FOUND();
				}
			} catch (DbxException.BadRequest e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_REQUEST();
			} catch (DbxException.BadResponse e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_RESPONSE();
			} catch (DbxException.InvalidAccessToken e) {
				e.printStackTrace();
				result = FileOpsStatus.INVALID_OAUTH_TOKEN();
			} catch (DbxException.NetworkIO e) {
				e.printStackTrace();
				result = FileOpsStatus.NETWORK_IO_ERROR();
			} catch (DbxException.ProtocolError e) {
				e.printStackTrace();
				result = FileOpsStatus.PROTOCOL_ERROR();
			} catch (DbxException.RetryLater e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_BUSY();
			} catch (DbxException.ServerError e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_ERROR();
			} catch (DbxException e) {
				e.printStackTrace();
				 FileOpsStatus.GENERIC_DROPBOX_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	public FileOpsStatus moveFile(Account source, RemoteFile remoteSource, String remoteDest){
		return moveFile(source, remoteSource.getPath(), remoteDest);
	}
	
	public FileOpsStatus deleteFile(Account source, String remoteSource){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				client.delete(remoteSource.replace('\\', '/'));
			} catch (DbxException.BadRequest e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_REQUEST();
			} catch (DbxException.BadResponse e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_RESPONSE();
			} catch (DbxException.InvalidAccessToken e) {
				e.printStackTrace();
				result = FileOpsStatus.INVALID_OAUTH_TOKEN();
			} catch (DbxException.NetworkIO e) {
				e.printStackTrace();
				result = FileOpsStatus.NETWORK_IO_ERROR();
			} catch (DbxException.ProtocolError e) {
				e.printStackTrace();
				result = FileOpsStatus.PROTOCOL_ERROR();
			} catch (DbxException.RetryLater e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_BUSY();
			} catch (DbxException.ServerError e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_ERROR();
			} catch (DbxException e) {
				e.printStackTrace();
				result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	public FileOpsStatus deleteFile(Account source, RemoteFile remoteSource){
		return deleteFile(source, remoteSource.getPath());
	}
	
	public FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource){
		return loadFolder(source, remoteSource, 1);
	}
	
	public FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource, int depth){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				DbxEntry.WithChildren folder = client.getMetadataWithChildren(remoteSource.getPath().replace('\\', '/'));
				if(folder != null){
					for(DbxEntry entry : folder.children){
						if(entry.isFolder()){
							remoteSource.addFolder(new RemoteFolder(entry.name, remoteSource, source));
						}else if(entry.isFile()){
							remoteSource.addFile(new RemoteFile(entry.name, remoteSource, source));
						}
					}
				}else{
					result = FileOpsStatus.FILE_NOT_FOUND();
				}
			} catch (DbxException.BadRequest e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_REQUEST();
			} catch (DbxException.BadResponse e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_RESPONSE();
			} catch (DbxException.InvalidAccessToken e) {
				e.printStackTrace();
				result = FileOpsStatus.INVALID_OAUTH_TOKEN();
			} catch (DbxException.NetworkIO e) {
				e.printStackTrace();
				result = FileOpsStatus.NETWORK_IO_ERROR();
			} catch (DbxException.ProtocolError e) {
				e.printStackTrace();
				result = FileOpsStatus.PROTOCOL_ERROR();
			} catch (DbxException.RetryLater e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_BUSY();
			} catch (DbxException.ServerError e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_ERROR();
			} catch (DbxException e) {
				e.printStackTrace();
				result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	public FileOpsStatus createFolder(Account source, RemoteFolder parent, String name){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				client.createFolder(parent.getPath().replace('\\', '/') + "/" + name);
			} catch (DbxException.BadRequest e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_REQUEST();
			} catch (DbxException.BadResponse e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_RESPONSE();
			} catch (DbxException.InvalidAccessToken e) {
				e.printStackTrace();
				result = FileOpsStatus.INVALID_OAUTH_TOKEN();
			} catch (DbxException.NetworkIO e) {
				e.printStackTrace();
				result = FileOpsStatus.NETWORK_IO_ERROR();
			} catch (DbxException.ProtocolError e) {
				e.printStackTrace();
				result = FileOpsStatus.PROTOCOL_ERROR();
			} catch (DbxException.RetryLater e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_BUSY();
			} catch (DbxException.ServerError e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_ERROR();
			} catch (DbxException e) {
				e.printStackTrace();
				result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	public FileOpsStatus copyFolder(Account source, RemoteFolder remoteSource, String remoteDest){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				if(folderExists(source, remoteSource.getPath())){
					client.copy(remoteSource.getPath(), remoteDest);
				}else{
					result = FileOpsStatus.FILE_NOT_FOUND();
				}
			} catch (DbxException.BadRequest e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_REQUEST();
			} catch (DbxException.BadResponse e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_RESPONSE();
			} catch (DbxException.InvalidAccessToken e) {
				e.printStackTrace();
				result = FileOpsStatus.INVALID_OAUTH_TOKEN();
			} catch (DbxException.NetworkIO e) {
				e.printStackTrace();
				result = FileOpsStatus.NETWORK_IO_ERROR();
			} catch (DbxException.ProtocolError e) {
				e.printStackTrace();
				result = FileOpsStatus.PROTOCOL_ERROR();
			} catch (DbxException.RetryLater e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_BUSY();
			} catch (DbxException.ServerError e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_ERROR();
			} catch (DbxException e) {
				e.printStackTrace();
				result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	public FileOpsStatus moveFolder(Account source, RemoteFolder remoteSource, String remoteDest){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				if(folderExists(source, remoteSource.getPath())){
					client.move(remoteSource.getPath(), remoteDest);
				}else{
					result = FileOpsStatus.FILE_NOT_FOUND();
				}
			} catch (DbxException.BadRequest e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_REQUEST();
			} catch (DbxException.BadResponse e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_RESPONSE();
			} catch (DbxException.InvalidAccessToken e) {
				e.printStackTrace();
				result = FileOpsStatus.INVALID_OAUTH_TOKEN();
			} catch (DbxException.NetworkIO e) {
				e.printStackTrace();
				result = FileOpsStatus.NETWORK_IO_ERROR();
			} catch (DbxException.ProtocolError e) {
				e.printStackTrace();
				result = FileOpsStatus.PROTOCOL_ERROR();
			} catch (DbxException.RetryLater e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_BUSY();
			} catch (DbxException.ServerError e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_ERROR();
			} catch (DbxException e) {
				e.printStackTrace();
				result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	public FileOpsStatus deleteFolder(Account source, RemoteFolder remoteSource){
		DbxClient client = getClient(source);
		FileOpsStatus result = FileOpsStatus.SUCCESS();
		
		if(client != null){
			try {
				client.delete(remoteSource.getPath());
				result = FileOpsStatus.SUCCESS();
			} catch (DbxException.BadRequest e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_REQUEST();
			} catch (DbxException.BadResponse e) {
				e.printStackTrace();
				result = FileOpsStatus.BAD_RESPONSE();
			} catch (DbxException.InvalidAccessToken e) {
				e.printStackTrace();
				result = FileOpsStatus.INVALID_OAUTH_TOKEN();
			} catch (DbxException.NetworkIO e) {
				e.printStackTrace();
				result = FileOpsStatus.NETWORK_IO_ERROR();
			} catch (DbxException.ProtocolError e) {
				e.printStackTrace();
				result = FileOpsStatus.PROTOCOL_ERROR();
			} catch (DbxException.RetryLater e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_BUSY();
			} catch (DbxException.ServerError e) {
				e.printStackTrace();
				result = FileOpsStatus.SERVER_ERROR();
			} catch (DbxException e) {
				e.printStackTrace();
				result = FileOpsStatus.GENERIC_DROPBOX_ERROR();
			}
		}else{		
			result = FileOpsStatus.DROPBOX_NULL_CLIENT();
		}
		return result;
	}
	
	private DbxClient getClient(Account source){
		String token = source.getAuth();
		
		if(token != null){
			return new DbxClient(new DbxRequestConfig("Workflow Client", Locale.getDefault().toString()), token);
		}else{
			return null;
		}
	}
	
	private boolean fileExists(Account source, String path){
		DbxClient client = getClient(source);
		if(client != null){
			DbxEntry entry;
			try {
				entry = client.getMetadata(path);
			} catch (DbxException e) {
				e.printStackTrace();
				return false;
			}
			return entry != null && entry.isFile();
		}else{
			return false;
		}
	}
	
	private boolean folderExists(Account source, String path){
		DbxClient client = getClient(source);
		if(client != null){
			DbxEntry entry;
			try {
				entry = client.getMetadata(path);
			} catch (DbxException e) {
				e.printStackTrace();
				return false;
			}
			return entry != null && entry.isFolder();
		}else{
			return false;
		}
	}
	
}
