package model.resource.remote;

import java.io.InputStream;
import java.util.ArrayList;

import model.account.Account;

public class RemoteFolder extends RemoteResource{

	private ArrayList<RemoteFile> files = null;
	private ArrayList<RemoteFolder> folders = null;
	
	public RemoteFolder(String n, RemoteFolder p, Account s) {
		super(n, p, s);
	}
	
	public ArrayList<RemoteFile> getFiles(){
		if(files == null){
			source.getFileOps().loadFolder(source, this);
		}
		return files;
	}
	
	public ArrayList<RemoteFolder> getFolders(){
		if(folders == null){
			source.getFileOps().loadFolder(source, this);
		}
		return folders;
	}
	
	public void clear(){
		files = null;
		folders = null;
	}
	
	public void addFile(RemoteFile f){
		if(files == null){
			files = new ArrayList<RemoteFile>();
		}
		if(folders == null){
			folders = new ArrayList<RemoteFolder>();
		}
		files.add(f);
	}
	
	public boolean containsFile(RemoteFile f){
		return getFiles().contains(f);
	}
	
	public void removeFile(RemoteFile f){
		getFiles().remove(f);
	}
	
	public void addFolder(RemoteFolder f){
		if(files == null){
			files = new ArrayList<RemoteFile>();
		}
		if(folders == null){
			folders = new ArrayList<RemoteFolder>();
		}
		folders.add(f);
	}
	
	public boolean containsFolder(RemoteFolder f){
		return getFolders().contains(f);
	}
	
	public void removeFolder(RemoteFolder f){
		getFolders().remove(f);
	}

	public boolean isDirectory() {
		return true;
	}

	public InputStream getIconResourceStream() {
		return RemoteFolder.class.getResourceAsStream("../../../resources/folder_16.png");
	}
	
}
