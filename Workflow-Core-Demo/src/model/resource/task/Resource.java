package model.resource.task;

import java.io.Serializable;

import view.library.IResourceView;
import controller.Core;
import controller.library.Library;
import controller.library.Task;

public abstract class Resource implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Task task;
	protected Library library; //the library this resource belongs to
	
	protected transient IResourceView view = null;
	
	protected Resource(Library l){
		library = l;
	}
	
	protected Resource(Resource r){
		task = r.getParentTask();
		library = r.getParentTask().getTaskList().getLibrary(); 
	}
	
	protected Resource(Task t){
		task = t;
		library = t.getTaskList().getLibrary();
	}
	
	//Get this resource's name
	public abstract String getName();
		
	public abstract void setName(String n);
	
	public IResourceView getView(){
		return view;
	}
	
	public void setView(IResourceView v){
		view = v;
	}
	
	public Task getParentTask(){
		return task;
	}
	
	public Library getParentLibrary(){
		return library;
	}
	
	public abstract String getCommandString();
	
	public boolean isLoaded(){
		return true;
	}
	
	public void save(){
	}
	
	public void load(){
	}
	
	public void open(){
		Core.getProcessMonitor().open(this);
	}
	
	//create a copy of the object representing this resource
	public abstract Resource copyObject();
	
	public abstract boolean storedAsFile();
	
}
