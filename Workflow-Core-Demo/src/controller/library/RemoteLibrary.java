package controller.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import view.chooser.Chooser;
import view.chooser.IRemoteChooserDialog;
import controller.io.FileOpsStatus;
import controller.io.LocalFileOps;
import controller.io.ResourceActionService;
import model.account.Account;
import model.resource.library.RemoteLibraryResource;
import model.resource.remote.RemoteFolder;
import model.resource.task.FileResource;
import model.resource.task.Resource;
import controller.Core;
import controller.SimpleHandler;

public class RemoteLibrary extends Library{

	private static final long serialVersionUID = 1L;
	
	public RemoteLibrary(String n, String r, String p, Account a){
		name = n;
		root = r;
		account = a;
		libResource = new RemoteLibraryResource(p, this);
	}

	public boolean saveTasks() {
		
		if(!Core.getResourceCache().containsFileRecord(libResource)){
			Core.getResourceCache().addFile(libResource);
		}
		
		File taskFile = new File(Core.getResourceCache().getPath(libResource));
		
		try {
			FileOutputStream fOut = new FileOutputStream(taskFile);
			ObjectOutputStream objOut = new ObjectOutputStream(fOut);
			
			ArrayList<TaskList> libData = libResource.getLibraryData();
			
			if(libData != null){
				objOut.writeObject(libData);
				objOut.close();
				fOut.close();
				return true;
			}else{
				objOut.close();
				fOut.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Core.getGuiFactory().displayError(null, null, "Library data could not be saved. The specified target location could not be found.", true);
		} catch (IOException e) {
			e.printStackTrace();
			Core.getGuiFactory().displayError(null, null, "Library data could not be saved. An error occurred during the write process.", true);
		}
		return false;
	}
	
	public boolean loadTasks() {
		
		if(Core.getResourceCache().containsFileData(libResource)){
			File taskFile = Core.getResourceCache().getFile(libResource);
			
			if(taskFile.exists() && taskFile.isFile()){
				try {
					FileInputStream fRead = new FileInputStream(taskFile);
					ObjectInputStream objRead = new ObjectInputStream(fRead);
					
					libResource.setLibraryData((ArrayList<TaskList>)objRead.readObject());
					
					for(TaskList tl : libResource.getLibraryData()){
						tl.setLibrary(this);
					}
					
					objRead.close();
					fRead.close();
					
					return true;
				} catch (FileNotFoundException e) { //this should never happen due to the checks above
					e.printStackTrace();
					Core.getGuiFactory().displayError(null, null, "This library's data file could not be located.", true);
				} catch (IOException e) { //read error
					e.printStackTrace();
					Core.getGuiFactory().displayError(null, null, "This library's data could not be loaded. An error occurred during the read process.", true);
				} catch (ClassNotFoundException e) { //corrupted/unexpected data in serialized object
					e.printStackTrace();
					Core.getGuiFactory().displayError(null, null, "This library's data could not be loaded. It may be corrupt or damaged.", true);
				}
				return false;
			}else{
				System.out.println("Couldn't find the library file at " + taskFile.getAbsolutePath());
			}
		}
		return false;
	}
	
	public void loadAllResources(SimpleHandler success, SimpleHandler failed){
		for(TaskList tl : libResource.getLibraryData()){
			for(Task t : tl.getModel()){
				for(Resource r : t.getResourceList().getModel()){
					r.load();
				}
			}
		}
	}
	
	public void loadResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed){
		ResourceActionService.loadResources(resources, success, failed);
	}
	
	public void saveAllResources(SimpleHandler success, SimpleHandler failed){
		for(TaskList tl : libResource.getLibraryData()){
			for(Task t : tl.getModel()){
				for(Resource r : t.getResourceList().getModel()){
					r.save();
				}
			}
		}
	}
	
	public void saveResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed){
		ResourceActionService.saveResources(resources, success, failed);
	}
	
	public void saveResource(FileResource resource, SimpleHandler success, SimpleHandler failed){
		if(resource.needsSave()){		
			ResourceActionService.saveResource(resource, success, failed);
		}
	}

	public boolean addResource(FileResource resource) {
		//resource is stored in a library
		//ask user where to store it remotely
		IRemoteChooserDialog dialog = Core.getGuiFactory().getRemoteChooserDialog(account, "", Chooser.Mode.MODE_SAVE_FILE, true, "Add Resource...", "Specify where " + resource.getName() + " should be stored.", root, resource.getName());
		if(dialog.showDialogAndWait() && !dialog.getSelection().isEmpty()){
			//get the resource's current path
			String source = Core.getResourceCache().getPath(resource);
			//set the resource's new path
			resource.setPath(dialog.getSelection().get(0).getPath());
			//add the resource to the cache
			String target = Core.getResourceCache().addFile(resource);
			//copy the file to that location
			new LocalFileOps().copyFile(null, source, target);
			return true;
		}else{
			return false;
		}
	}
	
	public FileResource addResourceFromFile(Task parent, String path){
		//resource is stored locally
		//ask user where to store it remotely
		String filename = path.substring(Math.max(0, path.lastIndexOf(File.separator)) + 1);
		IRemoteChooserDialog dialog = Core.getGuiFactory().getRemoteChooserDialog(account, "", Chooser.Mode.MODE_SAVE_FILE, true, "Add Resource...", "Specify where " + filename + " should be stored.", root, filename);

		if(dialog.showDialogAndWait() && !dialog.getString().isEmpty()){
			//create a new resource object with the new information
			FileResource newRes = new FileResource(parent, dialog.getString());
			//add the resource to the cache
			String target = Core.getResourceCache().addFile(newRes);
			//copy the file to that location
			new LocalFileOps().copyFile(null, path, target);
			return newRes;
		}else{
			return null;
		}
	}

	public FileResource copyResource(FileResource resource) {
		IRemoteChooserDialog dialog = Core.getGuiFactory().getRemoteChooserDialog(account, "", Chooser.Mode.MODE_SAVE_FILE, true, "Copy Resource", "Specify where " + resource.getName() + " should be copied to.", resource.getFolder(), resource.getName());
		
		if(dialog.showDialogAndWait() && !dialog.getSelection().isEmpty()){
			return copyResource(resource, trimRemoteResourcePath(dialog.getSelection().get(0).getPath()));
		}else{
			return null;
		}
		
	}

	public FileResource copyResource(FileResource resource, String destination) {
		//if the path is valid, perform the copy, else return null.
		if(validatePath(destination) && account.getFileOps().copyFile(account, resource.getAbsolutePath(), destination).getCode() == FileOpsStatus.Code.SUCCESS){
			FileResource ret = new FileResource(resource);
			ret.setPath(destination);
			return ret;
		}else{
			return null;
		}
	}

	public boolean moveResource(FileResource resource) {
		IRemoteChooserDialog dialog = Core.getGuiFactory().getRemoteChooserDialog(account, "", Chooser.Mode.MODE_SAVE_FILE, true, "Move Resource", "Specify where " + resource.getName() + " should be moved to.", resource.getFolder(), resource.getName());
		
		if(dialog.showDialogAndWait() && !dialog.getSelection().isEmpty()){
			return moveResource(resource, trimRemoteResourcePath(dialog.getSelection().get(0).getPath()));
		}else{
			return false;
		}
		
	}

	public boolean moveResource(FileResource resource, String destination) {
		if(validatePath(destination)){
			return account.getFileOps().moveFile(account, resource.getAbsolutePath(), destination).getCode() == FileOpsStatus.Code.SUCCESS;
		}else{
			return false;
		}
	}

	public boolean deleteResource(FileResource resource) {
		return account.getFileOps().deleteFile(account, resource.getAbsolutePath()).getCode() == FileOpsStatus.Code.SUCCESS;
	}

	public boolean locateResource(FileResource resource) {	
		//spawn a graphical interface to request a new path for the resource
		IRemoteChooserDialog dialog = Core.getGuiFactory().getRemoteChooserDialog(account, "", Chooser.Mode.MODE_SINGLE_FILE, false, "Missing Resource", "The resource " + resource.getName() + " cannot be located.\nPlease enter its location.", resource.getFolder(), resource.getName());
		
		if(dialog.showDialogAndWait() && !dialog.getSelection().isEmpty()){
			resource.setPath(trimRemoteResourcePath(dialog.getSelection().get(0).getPath()));
			return true;
		}else{
			return false;
		}
	}
	
	private String trimRemoteResourcePath(String s){
		String path = s;
		int loc = path.indexOf(root);
		path = path.substring(loc != -1 ? loc + root.length() : 0);
		return path;
	}
	
	public boolean loadFolder(RemoteFolder f) {
		return account.getFileOps().loadFolder(account, f).getCode() == FileOpsStatus.Code.SUCCESS;
	}
	
	public boolean createFolder(RemoteFolder parent, String name) {
		return account.getFileOps().createFolder(account, parent, name).getCode() == FileOpsStatus.Code.SUCCESS;
	}

	public boolean deleteFolder(RemoteFolder folder) {
		return account.getFileOps().deleteFolder(account, folder).getCode() == FileOpsStatus.Code.SUCCESS;
	}

	public boolean copyFolder(RemoteFolder source, String target) {
		return account.getFileOps().copyFolder(account, source, target).getCode() == FileOpsStatus.Code.SUCCESS;
	}

	public boolean moveFolder(RemoteFolder source, String target) {
		return account.getFileOps().moveFolder(account, source, target).getCode() == FileOpsStatus.Code.SUCCESS;
	}
	
	public Type getType(){
		return Type.REMOTE;
	}
	
	public boolean isRemovable(){
		return false;
	}
	
	public String getAuth(){
		return account.getAuth();
	}
	
	public Account getAccount(){
		return account;
	}
	
	public void setAccount(Account a){
		account = a;
	}
}
