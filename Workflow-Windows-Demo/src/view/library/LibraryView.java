package view.library;

import model.resource.library.LibraryResource;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import controller.Core;
import controller.library.Library;
import controller.library.TaskList;

public class LibraryView extends BorderPane implements ILibraryView{

	LibraryResource resource;
	
	SplitPane centerPane;
	BorderPane navPane;
	ListView<TaskList> taskListPane;
	Button newTaskListButton, saveTasksButton, uploadTasksButton, uploadResourcesButton;
	VBox empty;
	
	TaskListView curView = null;
	
	public LibraryView(LibraryResource r) {
		resource = r;
		generateUI();
		updateUI();
	}

	private void generateUI() {
		centerPane = new SplitPane();
		centerPane.setOrientation(Orientation.HORIZONTAL);

		navPane = new BorderPane();
		navPane.setPrefWidth(100);
		navPane.setMinWidth(100);
		
		centerPane.getItems().add(navPane);
		
		taskListPane = new ListView<TaskList>();
		taskListPane.setCellFactory(new Callback<ListView<TaskList>, ListCell<TaskList>>(){
			
			public ListCell<TaskList> call(ListView<TaskList> listView) {
				ListCell<TaskList> newCell = new ListCell<TaskList>(){
					
					ListCell<TaskList> cell = this;
					
					protected void updateItem(TaskList taskList, boolean empty){
						super.updateItem(taskList, empty);
						if(taskList == null || empty){
							setText("");
							setGraphic(null);
						}else{
							((TaskListView)taskList.getView()).setCell(this);
							setText(taskList.getName());
							setOnMouseClicked(new EventHandler<MouseEvent>(){
								public void handle(MouseEvent e) {
									if(e.getButton() == MouseButton.SECONDARY){
										((TaskListView)taskList.getView()).contextMenu().show(cell, Side.BOTTOM, 0, 0);
									}
								}
							});
						}
					}
				};
				return newCell;
			}
		});
		
		taskListPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TaskList>(){
			public void changed(ObservableValue<? extends TaskList> obs, TaskList oldSel, TaskList newSel) {
				if(newSel != null){
					setTaskListView(newSel.getView());
				}
			}
		});
		
		HBox newTaskListButtonPane = new HBox();
		newTaskListButtonPane.setPadding(new Insets(1, 1, 1, 1));
		newTaskListButton = new Button("New Task List");
		newTaskListButton.prefWidthProperty().bind(newTaskListButtonPane.widthProperty());
		newTaskListButton.setOnAction((event)->{
			Core.getGuiFactory().showNewTaskListDialog();
		});
		newTaskListButtonPane.getChildren().add(newTaskListButton);
		
		navPane.setTop(newTaskListButtonPane);
		navPane.setCenter(taskListPane);
		
		saveTasksButton = new Button("Save Task List Data");
		//saveTasksButton.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		saveTasksButton.setOnAction((event)->{
			resource.getParentLibrary().saveTasks();
		});
		
		uploadTasksButton = new Button("Upload Task List Data");
		//uploadTasksButton.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		uploadTasksButton.setOnAction((event)->{
			resource.getParentLibrary().saveResource(resource.getParentLibrary().getResource(), null, null);
		});
		
		uploadResourcesButton = new Button("Upload Changed Resources");
		//uploadResourcesButton.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		uploadResourcesButton.setOnAction((event)->{
			resource.getParentLibrary().saveAllResources(null, null);
		});
		
		GridPane buttonGrid = new GridPane();
		buttonGrid.prefWidthProperty().bind(this.widthProperty());
		GridPane.setHgrow(saveTasksButton, Priority.ALWAYS);
		GridPane.setVgrow(saveTasksButton, Priority.ALWAYS);
		saveTasksButton.maxWidthProperty().bind(buttonGrid.widthProperty());
		buttonGrid.add(saveTasksButton, 0, 0, 1, 1);
		if(resource.getParentLibrary().getType() == Library.Type.REMOTE){
			GridPane.setHgrow(uploadTasksButton, Priority.ALWAYS);
			GridPane.setVgrow(uploadTasksButton, Priority.ALWAYS);
			uploadTasksButton.maxWidthProperty().bind(buttonGrid.widthProperty());
			buttonGrid.add(uploadTasksButton, 1, 0, 1, 1);
			GridPane.setHgrow(uploadResourcesButton, Priority.ALWAYS);
			GridPane.setVgrow(uploadResourcesButton, Priority.ALWAYS);
			uploadResourcesButton.maxWidthProperty().bind(buttonGrid.widthProperty());
			buttonGrid.add(uploadResourcesButton, 0, 1, 2, 1);
		}
		
		
		setCenter(centerPane);
		setBottom(buttonGrid);
	}

	public void updateUI() {
		taskListPane.getItems().clear();
		taskListPane.getItems().addAll(resource.getLibraryData());
		if(resource.getLibraryData().isEmpty()){
			setTaskListView(null);
		}
	}
	
	public void setResource(LibraryResource lr){
		resource = lr;
		updateUI();
	}
	
	public void setTaskListView(ITaskListView tlv){
		//clear old view
		if(curView == null){
			centerPane.getItems().remove(getEmptyView());
		}else{
			centerPane.getItems().remove(curView);
		}
		//set new view
		curView = (TaskListView)tlv;
		if(curView == null){
			centerPane.getItems().add(getEmptyView());
		}else{
			centerPane.getItems().add(curView);
		}
		
		if(curView != null){
			//adjust the slider so the nav pane is 100 pixels wide.
			centerPane.setDividerPosition(0, Math.min(1.0, (100.0 / centerPane.getWidth())));
		}
	}
	
	private VBox getEmptyView(){
		if(empty == null){
			empty = new VBox();
			empty.getChildren().addAll(new Label("This library is empty."), new Label("Create a new task list to get started."));
			empty.setAlignment(Pos.CENTER);
		}
		return empty;
	}

}
