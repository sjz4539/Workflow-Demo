package controller.library;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import view.library.ILibraryMenu;
import view.library.ILibraryView;
import view.chooser.Chooser;
import controller.Core;
import controller.SimpleHandler;
import controller.io.FileOpsStatus;
import controller.io.LocalFileOps;
import controller.io.ResourceActionService;
import model.account.Account;
import model.resource.library.LibraryResource;
import model.resource.task.FileResource;

/**
 * Libraries represent storage locations.
 * They handle all I/O requests for data stored in the location they represent.
 * Subclasses representing remote library types also handle all cache operations for downloaded copies of data.
 * 
 * @author Test
 *
 */
public abstract class Library implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String RESOURCE_FILE_EXTENSION = ".wlr";
	public static final String RESOURCE_FILE_DESCRIPTION = "Workflow Library Resource";
	public static final String LIBRARY_FILE_EXTENSION = ".wfl";
	public static final String LIBRARY_FILE_DESCRIPTION = "Workflow Library";
	public static final String REMOVABLE_LIBRARY_FILE_EXTENSION = ".wrl";
	public static final String REMOVABLE_LIBRARY_FILE_DESCRIPTION = "Removable Workflow Library";
	public static final String DEFAULT_LIBRARY_FILENAME = "WorkflowLibrary";
	
	protected String name = ""; //this library's name
	protected int type = -1; //the type of this library
	protected String root = ""; //the root folder of this library
	protected LibraryResource libResource; //the resource object representing this library's data
	protected Account account;
	
	protected transient ILibraryView libraryView;
	protected transient ILibraryMenu menu;
	protected transient boolean hasChanges = false; //flag indicating whether this library needs to be written to storage
	
	public enum Type{
		LOCAL, REMOTE
	}
	
	public boolean isNew(){
		return libResource.isNew();
	}
	
	public boolean isLoaded(){
		return libResource.getLibraryData() != null;
	}
	
	public abstract boolean isRemovable();
	
	public boolean hasChanges(){
		return hasChanges;
	}
	
	public void setHasChanges(boolean changes){
		hasChanges = changes;
	}
	
	public abstract Account getAccount();
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public String getRoot(){
		return root;
	}
	
	public abstract Type getType();
	
	public ILibraryView getView(){
		if(libraryView == null){
			libraryView = Core.getGuiFactory().getLibraryView(this);
		}
		return libraryView;
	}
	
	public void setView(ILibraryView view){
		libraryView = view;
	}
	
	public void updateUI(){
		if(libraryView == null){
			libraryView.updateUI();
		}
	}
	
	public void clearLibraryData(){
		libResource.clearLibraryData();
	}
	
	public LibraryResource getResource(){
		return libResource;
	}
	
	public ArrayList<TaskList> getLibraryData(){
		return libResource.getLibraryData();
	}
	
	public void setLibraryData(ArrayList<TaskList> ld){
		libResource.setLibraryData(ld);
	}
	
	//add task lists to this library
	public void addTaskList(TaskList tl){
		getLibraryData().add(tl);
		updateUI();
	}
	public void addTaskLists(List<TaskList> tlv){
		getLibraryData().addAll(tlv);
		updateUI();
	}
	
	public void copyTaskList(TaskList tl){
		//TODO
		/*
		String name = tl.getName();
		if(Core.getGuiFactory().requestTextInput("","","",name)){
			tl.setName(name);
		}
		*/
	}
	
	//remove task lists  from this library
	public void removeTaskList(TaskList tl){
		//should probably confirm this first.
		getLibraryData().remove(tl);
		updateUI();
	}
	public void removeTaskLists(List<TaskList> tlv){
		//should probably confirm this first.
		getLibraryData().removeAll(tlv);
		updateUI();
	}

	//save all current task lists to a file in this library's storage location
	public abstract boolean saveTasks();
	
	//get all task lists from this library's storage location
	public abstract boolean loadTasks();
	
	//save all files of all of this library's tasks to this library's storage location
	public abstract void saveAllResources(SimpleHandler success, SimpleHandler failed);
	
	//save the specified files to this library's storage location
	public abstract void saveResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed);
	
	//save the specified file to this library's storage location
	public abstract void saveResource(FileResource resource, SimpleHandler success, SimpleHandler failed);
	
	//save the specified file to this library's storage location
	public void saveResource(FileResource resource){
		saveResource(resource, null, null);
	}
	
	//get the actual contents of all files of all of this library's tasks from this library's storage location
	public abstract void loadAllResources(SimpleHandler success, SimpleHandler failed);
	
	//get the actual contents of the specified files from this library's storage location
	public abstract void loadResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed);
	
	//get the actual contents of the specified file from this library's storage location
	public void loadResource(FileResource resource, SimpleHandler success, SimpleHandler failed){
		ResourceActionService.loadResource(resource, success, failed);
	}
	
	//get the actual contents of the specified file from this library's storage location
	public void loadResource(FileResource resource){
		loadResource(resource, null, null);
	}
	
	public abstract boolean addResource(FileResource resource);
	
	public abstract FileResource addResourceFromFile(Task parent, String path);
	
	public abstract FileResource copyResource(FileResource resource);
	
	public abstract FileResource copyResource(FileResource resource, String destination);
	
	public abstract boolean moveResource(FileResource resource);
	
	public abstract boolean moveResource(FileResource resource, String destination);
	
	public abstract boolean deleteResource(FileResource resource);
	
	public abstract boolean locateResource(FileResource resource);
	
	public boolean exportResource(FileResource r){
		if(r.isLoaded()){
			
			File file = new File(r.getAbsolutePath());
			
			List<File> selection = Core.getGuiFactory().showNativeFileChooser(Chooser.Mode.MODE_SAVE_FILE, "Select Export Location...", file);
			
			if(!selection.isEmpty()){
				return exportResource(r, selection.get(0).getAbsolutePath());
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public boolean exportResource(FileResource r, String destination){
		return LocalFileOps.copyFile(r.getAbsolutePath(), destination).getCode() == FileOpsStatus.Code.SUCCESS;
	}
	
	protected boolean validatePath(String p){
		return p.startsWith(root);
	}
	
	protected boolean validatePath(FileResource r){
		return r.getParentLibrary().equals(this) && r.getAbsolutePath().startsWith(root);
	}
	
	public ILibraryMenu getMenu(){
		if(menu == null){
			menu = Core.getGuiFactory().getLibraryMenu(this);
		}
		return menu;
	}
	
	public void setMenu(ILibraryMenu m){
		menu = m;
	}
	
	public void updateStatus(String m){
		if(menu != null){
			menu.updateStatus(m);
		}
	}
	
}
