package view.library;

import java.util.Optional;

import controller.library.Task;
import controller.library.TaskList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TaskListView extends ScrollPane implements ITaskListView{
	
	private TaskList taskList;
	private HBox addTaskPane;
	private VBox listPane;
	private ContextMenu menu;
	
	private ListCell<TaskList> cell = null;
	
	public TaskListView(TaskList tl){
		taskList = tl;
		generateUI();
		updateUI(true);
	}
	
	private void generateUI(){
		
		//create the add task pane
		addTaskPane = new HBox();
		addTaskPane.setPadding(new Insets(1, 1, 1, 1));
		Button addTaskButton = new Button("Add a new task");
		addTaskButton.prefWidthProperty().bind(addTaskPane.widthProperty());
		addTaskButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				taskList.newTask();
			}
		});
		
		addTaskPane.getChildren().add(addTaskButton);
		
		//create the list pane
		listPane = new VBox();
		listPane.getChildren().add(addTaskPane);
		listPane.setSpacing(2);
		
		//scrollPane.setFitToHeight(true);
		setFitToWidth(true);
		setMinWidth(200);
		setVbarPolicy(ScrollBarPolicy.ALWAYS);
		setContent(listPane);

	}
	
	public void updateUI(boolean updateTaskList){

		if(updateTaskList){
			listPane.getChildren().clear();
			listPane.getChildren().add(addTaskPane);
			for(Task task : taskList.getModel()){
				listPane.getChildren().add((TaskView)task.getView());
			}
		}
		
	}
	
	public TaskList getTaskList(){
		return taskList;
	}
	
	public void setTaskList(TaskList tl){
		taskList = tl;
		updateUI(true);
	}
	
	public boolean containsTask(Task t){	
		return getChildren().contains(t.getView());
	}
	
	public void setCell(ListCell<TaskList> c){
		cell = c;
	}
	
	public void updateStatus(boolean error){
		if(cell != null){
			if(error){
				cell.setGraphic(new ImageView(new Image(IResourceView.class.getResourceAsStream("../../resources/warning_16.png"))));
				Tooltip.install(cell.getGraphic(), new Tooltip("A resource in this list has a problem."));
			}else{
				cell.setGraphic(null);
			}
		}
	}
	
	public ContextMenu contextMenu(){
		
		if(menu == null){
			
			MenuItem editItem = new MenuItem("Change Name");
			editItem.setOnAction(
				(event)->{
					TextInputDialog dialog = new TextInputDialog(taskList.getName());
					dialog.setTitle("Enter a new name");
					
					Optional<String> result = dialog.showAndWait();
					
					if(result.isPresent() && result.get().length() > 0){
						taskList.setName(result.get());
					}
				}
			);
			MenuItem copyItem = new MenuItem("Copy");
			copyItem.setOnAction(
				(event)->{
					taskList.getLibrary().copyTaskList(taskList);
				}
			);
			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction(
				(event)->{
					taskList.getLibrary().removeTaskList(taskList);
				}
			);
			
			menu = new ContextMenu();
			menu.getItems().addAll(editItem, copyItem, deleteItem);
			
		}
		
		return menu;

	}
	
}
