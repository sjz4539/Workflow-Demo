package controller.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import controller.Core;
import model.resource.task.FileResource;
import model.resource.task.Resource;
import view.library.IResourceListView;

public class ResourceList implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private transient IResourceListView resourceListView;
	private transient ArrayList<FileResource> errorList = new ArrayList<FileResource>();
	
	private ArrayList<Resource> resourceList;
	private Task parent;
	
	public ResourceList(Task p){
		parent = p;
		resourceList = new ArrayList<Resource>();
	}
	
	//copy constructor that assigns parent task based on source
	public ResourceList(ResourceList rl){
		parent = rl.getParentTask();
		resourceList = new ArrayList<Resource>();
		for(Resource r : rl.getModel()){
			resourceList.add(r.copyObject());
		}
	}
	
	//copy constructor that assigns specific parent task
	public ResourceList(Task t, ResourceList rl){
		this(rl);
		parent = t;
	}
	
	public Task getParentTask(){
		return parent;
	}

	public ArrayList<Resource> getModel(){
		return resourceList;
	}
	
	public void setModel(ArrayList<Resource> rl){
		resourceList = rl;
		updateUI();
	}
	
	public IResourceListView getView(){
		if(resourceListView == null){
			resourceListView = Core.getGuiFactory().getResourceListView(this);
		}
		return resourceListView;
	}
	
	public void setView(IResourceListView rlv){
		resourceListView = rlv;
	}
	
	public void updateUI(){
		if(resourceListView != null){
			resourceListView.updateUI();
		}
	}
	
	public Resource getResource(int i){
		return resourceList.get(i);
	}
	
	public void addResource(Resource r){
		resourceList.add(r);
		updateUI();
	}
	
	public void addResource(FileResource r){
		if(getParentTask().getTaskList().getLibrary().addResource(r)){
			resourceList.add(r);
			updateUI();
		}
	}
	
	public void addResourceFromFile(Task parent, String path){
		FileResource newRes = getParentTask().getTaskList().getLibrary().addResourceFromFile(parent, path);
		if(newRes != null){
			resourceList.add(newRes);
			updateUI();
		}
	}
	
	public void addResources(List<Resource> rl){
		for(Resource r : rl){
			resourceList.add(r);
		}
		updateUI();
	}
	
	public Resource removeResource(int i){
		Resource ret = resourceList.remove(i);
		updateUI();
		return ret;
	}
	
	public void removeResource(Resource r){
		resourceList.remove(r);
		updateUI();
	}
	
	public void childStatusChanged(FileResource r, boolean error){
		if(error && !errorList.contains(r)){
			errorList.add(r);
		}else{
			errorList.remove(r);
		}
		if(resourceListView != null){
			resourceListView.updateStatus(!errorList.isEmpty());
		}
		getParentTask().childStatusChanged(!errorList.isEmpty());
	}
	
}
