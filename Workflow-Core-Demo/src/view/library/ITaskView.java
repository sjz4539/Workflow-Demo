package view.library;

import controller.library.Task;

public interface ITaskView{

	//update this task view to match the task's information
	public void updateUI();
	
	//get the task object assigned to this task view
	public Task getTask();
	
	//set the task object assigned to this task view
	public void setTask(Task t);
	
	//update the UI element used to display this task's status
	public void updateStatus(boolean error);
	
}
