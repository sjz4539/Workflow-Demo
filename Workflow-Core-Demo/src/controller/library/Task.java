package controller.library;

import java.io.Serializable;

import controller.Core;
import view.library.ITaskView;

public class Task implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private transient ITaskView taskView;
	
	private TaskList taskList;
	private ResourceList resourceList;
	private String name, description, time, date;
	
	//default constructor, no parent tasklist reference
	private Task(){
		name = new String("New Task");
		description = new String("Important things!");
		time = new String("Some time");
		date = new String("Some day");
		resourceList = new ResourceList(this);
	}
	
	//default constructor, assigns given parent tasklist reference
	public Task(TaskList tl){
		this();
		taskList = tl;
	}
	
	//complete constructor, no parent tasklist reference
	private Task(String n, String desc, String t, String d){
		name = new String(n);
		description = new String(desc);
		time = new String(t);
		date = new String(d);
		resourceList = new ResourceList(this);
	}
	
	//complete constructor, assigns given parent tasklist reference
	public Task(TaskList tl, String n, String desc, String t, String d){
		this(n, desc, t, d);
		taskList = tl;
	}
	
	//copy constructor, no parent tasklist reference
	private Task(Task t){
		name = new String(t.getName());
		description = new String(t.getDescription());
		time = new String(t.getTime());
		date = new String(t.getDate());
		resourceList = new ResourceList(t.getResourceList());
	}
	
	//Copy constructor, assigns given parent tasklist reference
	public Task(TaskList tl, Task t){
		this(t);
		taskList = tl;
	}
	
	//fetch parent tasklist reference
	public TaskList getTaskList(){
		return taskList;
	}
	
	//set parent tasklist reference
	public void setTaskList(TaskList tl){
		taskList = tl;
	}
	
	public ResourceList getResourceList(){
		return resourceList;
	}
	
	public void setResourceList(ResourceList rl){
		resourceList = rl;
	}
	
	//fetch TaskView used to display this object
	public ITaskView getView(){
		if(taskView == null){
			taskView = Core.getGuiFactory().getTaskView(this);
		}
		return taskView;
	}
	
	//set TaskView used to display this object
	public void setView(ITaskView tv){
		taskView = tv;
	}
	
	public void childStatusChanged(boolean error){
		if(taskView != null){
			taskView.updateStatus(error);
		}
		getTaskList().childStatusChanged(this, error);
	}
	
	//===============
	//MODEL FUNCTIONS
	//===============
	
	public void setName(String n){
		name = n;
	}
	
	public String getName(){
		return name;
	}
	
	public void setDescription(String d){
		description = d;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDate(String d){
		date = d;
	}
	
	public String getDate(){
		return date;
	}
	
	public void setTime(String t){
		time = t;
	}
	
	public String getTime(){
		return time;
	}
	
	public void copy(){
		taskList.copyTask(this);
	}
	
	public void delete(){
		taskList.deleteTask(this);
	}
	
}
