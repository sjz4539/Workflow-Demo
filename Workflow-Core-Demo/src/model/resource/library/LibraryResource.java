package model.resource.library;

import java.util.ArrayList;

import model.resource.task.FileResource;
import controller.library.Library;
import controller.library.TaskList;

public abstract class LibraryResource extends FileResource{

	private static final long serialVersionUID = 1L;
	
	protected transient ArrayList<TaskList> libraryData;
	
	protected boolean isNew;
	
	public LibraryResource(String p, Library l){
		super(l);
		path = (p.endsWith(Library.RESOURCE_FILE_EXTENSION) ? p : p + Library.RESOURCE_FILE_EXTENSION);
		isNew = true;
	}
	
	public String getName(){
		return library.getName();
	}
	
	public LibraryResource copyObject(){
		return null;
	}
	
	public void clearLibraryData(){
		libraryData = new ArrayList<TaskList>();
	}
	
	public ArrayList<TaskList> getLibraryData() {
		//if the library is new and hasn't had its model created yet, do so.
		if(isNew && libraryData == null){
			libraryData = new ArrayList<TaskList>();
		}
		
		isNew = false;

		return libraryData;
	}
	
	public void setLibraryData(ArrayList<TaskList> ld){
		libraryData = ld;
	}
	
	public void setStatus(Status s, String m){
		status = s;
		library.updateStatus(m);
	}
	
	public boolean isNew(){
		return isNew;
	}
	
}
