package controller.library;

import java.io.Serializable;
import java.util.ArrayList;

import controller.Core;
import view.library.ITaskListView;

public class TaskList implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private transient ITaskListView taskListView;

	private transient Library library;
	private transient ArrayList<Task> errorList;
	
	private String name;
	private ArrayList<Task> taskList;
	
	public TaskList(Library l){
		this(l, "New Tasklist");
	}
	
	public TaskList(Library l, String n){
		library = l;
		name = new String(n);
		taskList = new ArrayList<Task>();
	}
	
	public TaskList(TaskList tl){
		library = tl.getLibrary();
		name = new String(tl.getName());
		taskList = new ArrayList<Task>();
		for(Task t : tl.getModel()){
			taskList.add(new Task(this, t));
		}
	}
	
	public TaskList(Library l, TaskList tl){
		library = l;
		name = new String(tl.getName());
		taskList = new ArrayList<Task>();
		for(Task t : tl.getModel()){
			taskList.add(new Task(this, t));
		}
	}
	
	public ITaskListView getView(){
		if(taskListView == null){
			taskListView = Core.getGuiFactory().getTaskListView(this);
		}
		return taskListView;
	}
	
	public void setView(ITaskListView tlv){
		taskListView = tlv;
	}
	
	public void updateUI(boolean updateTaskList){
		if(taskListView != null){
			taskListView.updateUI(updateTaskList);
		}
	}
	
	public Library getLibrary(){
		return library;
	}
	
	public void setLibrary(Library l){
		library = l;
	}
	
	public ArrayList<Task> getModel(){
		return taskList;
	}
	
	public void setModel(ArrayList<Task> tlm){
		taskList = tlm;
		updateUI(true);
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
		updateUI(true);
	}
	
	public Task newTask(){
		Task newTask = new Task(this);
		taskList.add(newTask);
		//newTask.setMode(true);
		updateUI(true);
		return newTask;
	}
	
	public void addTask(Task t){
		t.setTaskList(this);
		taskList.add(t);
	}
	
	public Task addTask(String name, String description, String time, String date){
		Task newTask = new Task(this, name, description, time, date);
		taskList.add(newTask);
		updateUI(true);
		return newTask;
	}
	
	public void deleteTask(Task t){
		taskList.remove(t);
		updateUI(true);
	}
	
	//Inserts a copy of the given task. If this list contains
	//the task being copied, the copy is inserted behind it,
	//otherwise it is appended to the list's end.
	public Task copyTask(Task t){
		Task copy = new Task(this, t);
		if(taskList.contains(t)){
			taskList.add(taskList.indexOf(t), copy);
		}else{
			taskList.add(copy);
		}
		updateUI(true);
		return copy;
	}
	
	public void childStatusChanged(Task t, boolean error){
		if(errorList == null){
			errorList = new ArrayList<Task>();
		}
		if(error && !errorList.contains(t)){
			errorList.add(t);
		}else{
			errorList.remove(t);
		}
		if(taskListView != null){
			taskListView.updateStatus(!errorList.isEmpty());
		}
	}
	
}
