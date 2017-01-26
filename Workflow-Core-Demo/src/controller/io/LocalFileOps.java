package controller.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import model.account.Account;
import model.resource.remote.RemoteFile;
import model.resource.remote.RemoteFolder;

public class LocalFileOps extends FileOps{
	
	public FileOpsStatus moveFile(Account account, RemoteFile source, String destination){
		return FileOpsStatus.NOT_ALLOWED(); //not possible for this fileops type
	}
	
	public FileOpsStatus moveFile(Account account, String source, String destination){
		return moveFile(source, destination);
	}
	
	public static FileOpsStatus moveFile(String source, String destination){
		File sourcePath = new File(source);
		File destPath = new File(destination);
		if(!destPath.exists() || requestOverwrite(destination)){
			return (sourcePath.renameTo(destPath) ? FileOpsStatus.SUCCESS() : FileOpsStatus.LOCAL_IO_ERROR());
		}else{
			return FileOpsStatus.FILE_ALREADY_EXISTS();
		}
	}
	
	public FileOpsStatus copyFile(Account account, RemoteFile source, String destination){
		return FileOpsStatus.NOT_ALLOWED(); //not possible for this fileops type
	}
	
	public FileOpsStatus copyFile(Account account, String source, String destination){
		return copyFile(source, destination);
	}
	
	public static FileOpsStatus copyFile(String source, String destination){
		File sourcePath = new File(source);
		File destPath = new File(destination);
		try {
			if(!destPath.exists() || requestOverwrite(destination)){
				
				FileInputStream in = new FileInputStream(sourcePath);
				FileOutputStream out = new FileOutputStream(destPath);
				
				byte[] buffer = new byte[2048];
				int size;
				
				while((size = in.read(buffer)) > 0){
					out.write(buffer, 0, size);
				}
				
				in.close();
				out.close();
				
				return FileOpsStatus.SUCCESS();
				
			}else{
				return FileOpsStatus.FILE_ALREADY_EXISTS();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return FileOpsStatus.LOCAL_IO_ERROR();
		}
	}
	
	public FileOpsStatus deleteFile(Account source, RemoteFile remoteSource) {
		return FileOpsStatus.NOT_ALLOWED(); //not possible for this fileops type
	}
	
	public FileOpsStatus deleteFile(Account account, String source){
		return deleteFile(source);
	}
	
	public static FileOpsStatus deleteFile(String source){
		if(new File(source).delete()){
			return FileOpsStatus.SUCCESS();
		} else {
			return FileOpsStatus.LOCAL_IO_ERROR();
		}
	}

	public FileOpsStatus loadFile(Account source, String remoteSource, String localDest) {
		return FileOpsStatus.SUCCESS(); //All data stored on local disk, saving is not done via fileops.
	}

	public FileOpsStatus saveFile(Account source, String localSource, String remoteDest) {
		return FileOpsStatus.NOT_ALLOWED(); //not possible for this fileops type
	}

	public FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource) {
		return FileOpsStatus.NOT_ALLOWED(); //use filechooser instead
	}

	@Override
	public FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource, int depth) {
		return FileOpsStatus.NOT_ALLOWED(); //use filechooser instead
	}

	@Override
	public FileOpsStatus createFolder(Account source, RemoteFolder parent, String name) {
		return FileOpsStatus.NOT_ALLOWED(); //not possible for this fileops type
	}

	@Override
	public FileOpsStatus copyFolder(Account source, RemoteFolder remoteSource, String remoteDest) {
		return FileOpsStatus.NOT_ALLOWED(); //not possible for this fileops type
	}

	@Override
	public FileOpsStatus moveFolder(Account source, RemoteFolder remoteSource, String remoteDest) {
		return FileOpsStatus.NOT_ALLOWED(); //not possible for this fileops type
	}

	@Override
	public FileOpsStatus deleteFolder(Account source, RemoteFolder remoteSource) {
		return FileOpsStatus.NOT_ALLOWED(); //not possible for this fileops type
	}
	
}
