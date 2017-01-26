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
import view.chooser.ILocalChooserDialog;
import model.account.Account;
import model.resource.library.LocalLibraryResource;
import model.resource.task.FileResource;
import controller.Core;
import controller.SimpleHandler;
import controller.io.FileOpsStatus;

public class LocalLibrary extends Library{

	private static final long serialVersionUID = 1L;
	
	public LocalLibrary(String n, String r, String p){
		name = n;
		root = r;
		libResource = new LocalLibraryResource(p, this);
	}
	
	public Account getAccount(){
		if(account == null){
			account = new Account(name, Account.AccountType.ACCOUNT_TYPE_LOCAL);
		}
		return account;
	}
	
	public boolean saveTasks() {
		String location = root + File.separator + libResource.getPath();
		
		java.io.File taskFile = new java.io.File(location);
		
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
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loadTasks() {
		String location = root + File.separator + libResource.getPath();

		java.io.File taskFile = new java.io.File(location);
		
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
				return false;
			} catch (IOException e) { //read error
				e.printStackTrace();
				return false;
			} catch (ClassNotFoundException e) { //corrupted/unexpected data in serialized object
				e.printStackTrace();
				return false;
			}
		}else{
			System.out.println("Couldn't find the library file at " + location);
			return false;
		}
	}

	//save all files of all of this library's tasks to this library's storage location
	public void saveAllResources(SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}
	
	//save the specified files to this library's storage location
	public void saveResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}
	
	//save a resource's data
	public void saveResource(FileResource resource, SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}

	//get the actual contents of all files of all of this library's tasks from this library's storage location
	public void loadAllResources(SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}
	
	//get the actual contents of the specified files from this library's storage location
	public void loadResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}
	
	//add a resource to this library
	public boolean addResource(FileResource resource) {
		//compare the resource's path against the root of this library
		//if the resource is not a child, call moveResource and then deleteResource if the move fails.
		if(validatePath(resource)){
			return true;
		}else{
			//need to copy this resource into the library
			FileResource copy = copyResource(resource);
			if(copy != null){
				//Copied successfully, update the provided resource object's path
				resource.setPath(copy.getPath());
				return true;
			}else{
				return false;
			}
		}
	}
	
	public FileResource addResourceFromFile(Task parent, String path){
		return null;
	}
	
	//copy the file that holds the resource's data on the local disk to a location the user will provide
	public FileResource copyResource(FileResource resource){
		ILocalChooserDialog dialog = Core.getGuiFactory().getLocalChooserDialog("", Chooser.Mode.MODE_SINGLE_FILE, false, "Select a destination...", "Choose a location to copy " + resource.getName() + " to.", resource.getFolder(), resource.getName());
		
		if(dialog.showDialogAndWait() && !dialog.getString().isEmpty()){
			//copy the file to that location
			return copyResource(resource, dialog.getString());
		}else{
			return null;
		}
	}
	
	//copy the file that holds the resource's data on the local disk to the specified location
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
	
	//move the file that holds the resource's data on the local disk to a location the user will provide
	public boolean moveResource(FileResource resource){
		ILocalChooserDialog dialog = Core.getGuiFactory().getLocalChooserDialog("", Chooser.Mode.MODE_SINGLE_FILE, false, "Select a destination...", "Choose a location to move " + resource.getName() + " to.", resource.getFolder(), resource.getName());
		
		if(dialog.showDialogAndWait() && !dialog.getString().isEmpty()){
			return moveResource(resource, dialog.getString());
		}else{
			return false;
		}
	}

	//move the file that holds the resource's data on the local disk to the specified location
	public boolean moveResource(FileResource resource, String destination) {
		//ask the user for a location to move the resource to
		//if the path is not a child of this library's root, request a new path
		if(validatePath(destination) && account.getFileOps().moveFile(account, resource.getAbsolutePath(), destination).getCode() == FileOpsStatus.Code.SUCCESS){
			resource.setPath(destination);
			return true;
		}else{
			return false;
		}
	}

	//delete the file that holds the resource's data on the local disk
	public boolean deleteResource(FileResource resource) {
		return account.getFileOps().deleteFile(account, resource.getAbsolutePath()).getCode() == FileOpsStatus.Code.SUCCESS;
	}

	//locate the file that holds the resource's data on the local disk
	public boolean locateResource(FileResource resource) {
		//spawn a graphical interface to request a new path for the resource
		ILocalChooserDialog dialog = Core.getGuiFactory().getLocalChooserDialog("", Chooser.Mode.MODE_SINGLE_FILE, false, "Missing Resource", "The resource " + resource.getName() + " cannot be located.\nPlease enter its location.", resource.getFolder(), resource.getName());
		
		if(dialog.showDialogAndWait()){
			List<File> selection = dialog.getSelection();
			if(!selection.isEmpty()){
				String path = selection.get(0).getAbsolutePath();
				int loc = path.indexOf(root);
				path = path.substring(loc != -1 ? loc + root.length() : 0);
				resource.setPath(path);
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public Type getType(){
		return Type.LOCAL;
	}
	
	public boolean isRemovable(){
		return false;
	}

}
