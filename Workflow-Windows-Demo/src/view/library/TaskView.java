package view.library;

import controller.library.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TaskView extends TitledPane implements ITaskView{

	private Task task;
	
	private BorderPane taskPaneView, taskPaneEdit;
	private StackPane viewEditPane;
	private ResourceListView resourceListPane;
	private VBox centerPaneView, centerPaneEdit, containerPane;
	private ToolBar toolbarView, toolbarEdit;
	private Button edit, save, cancel, delete, copy;
	private Label description, time, date;
	private TextField nameField, descriptionField, timeField, dateField;
	
	//contructor
	public TaskView(Task t){
		task = t;
		generateUI();
		updateUI();
	}
	
	//get the task object assigned to this task view
	public Task getTask(){
		return task;
	}
	
	//set the task object assigned to this task view
	public void setTask(Task t){
		task = t;
		updateUI();
	}
	
	//generate and layout gui elements
	private void generateUI(){
		//create elements
		description = new Label();
		time = new Label();
		date = new Label();
		
		edit = new Button("Edit");
		save = new Button("Save");
		delete = new Button("Delete");
		copy = new Button("Copy");
		cancel = new Button("Cancel");
		
		edit.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				editTask();
			}
		});
		save.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				confirmUpdateTask();
			}
		});
		delete.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				task.delete();
			}
		});
		copy.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				task.copy();
			}
		});
		cancel.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				cancelUpdateTask();
			}
		});
		
		nameField = new TextField(task.getName());
		descriptionField = new TextField(task.getDescription());
		timeField = new TextField(task.getTime());
		dateField = new TextField(task.getDate());
		
		//assemble the view mode gui
		toolbarView = new ToolBar();
		toolbarView.getItems().addAll(edit, delete, copy);
		toolbarView.setOrientation(Orientation.VERTICAL);
		centerPaneView = new VBox();
		centerPaneView.getChildren().addAll(description, time, date);
		centerPaneView.setPadding(new Insets(0,0,0,4));

		taskPaneView = new BorderPane();
		taskPaneView.setLeft(toolbarView);
		taskPaneView.setCenter(centerPaneView);
		taskPaneView.setVisible(true);
		
		//assemble the edit mode gui
		toolbarEdit = new ToolBar();
		toolbarEdit.getItems().addAll(save, cancel);
		toolbarEdit.setOrientation(Orientation.VERTICAL);
		centerPaneEdit = new VBox();
		centerPaneEdit.getChildren().addAll(nameField, descriptionField, timeField, dateField);
		centerPaneEdit.setPadding(new Insets(0,0,0,4));
		
		taskPaneEdit = new BorderPane();
		taskPaneEdit.setLeft(toolbarEdit);
		taskPaneEdit.setCenter(centerPaneEdit);	
		taskPaneEdit.setVisible(false);
		
		//create the file gui
		resourceListPane = (ResourceListView)task.getResourceList().getView();
		
		//assemble the two modes' elements
		viewEditPane = new StackPane();
		viewEditPane.getChildren().addAll(taskPaneView, taskPaneEdit);
		
		containerPane = new VBox();
		containerPane.getChildren().addAll(viewEditPane, resourceListPane);
		containerPane.setPadding(new Insets(0,0,2,0));
		
		setContent(containerPane);

	}

	//update this task view to match the task's information
	public void updateUI(){	
		//set title
		setText(task.getName());
		
		//set text values
		description.setText(task.getDescription());
		time.setText("Due Date: " + task.getTime());
		date.setText("Due Time: " + task.getDate());
		
		//fill text editors
		descriptionField.setText(task.getDescription());
		timeField.setText(task.getTime());
		dateField.setText(task.getDate());
		
		//don't display empty view fields
		description.setVisible(!(task.getDescription().isEmpty()));
		time.setVisible(!(task.getTime().isEmpty()));
		date.setVisible(!(task.getDate().isEmpty()));
		
	}
	
	//set this task view to view or edit mode
	public void setMode(boolean edit){
		taskPaneView.setVisible(!edit);
		taskPaneEdit.setVisible(edit);
		containerPane.layout();
	}
	
	public void editTask(){
		setMode(true);
	}
	
	public void cancelUpdateTask(){
		updateUI();
		setMode(false);
	}
	
	public void confirmUpdateTask(){
		task.setName(nameField.getText());
		task.setDescription(descriptionField.getText());
		task.setTime(timeField.getText());
		task.setDate(dateField.getText());
		updateUI();
		setMode(false);
	}
	
	public void updateStatus(boolean error){
		if(error){
			setGraphic(new ImageView(new Image(IResourceView.class.getResourceAsStream("../../resources/warning_16.png"))));
			Tooltip.install(getGraphic(), new Tooltip("One of this task's resources has a problem."));
		}else{
			setGraphic(null);
		}
	}
	
}
