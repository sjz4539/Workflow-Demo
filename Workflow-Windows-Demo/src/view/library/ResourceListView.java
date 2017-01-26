package view.library;

import java.io.File;
import java.util.Optional;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;

import controller.library.ResourceList;
import model.resource.task.FileResource;
import model.resource.task.Resource;
import model.resource.task.WebResource;
import view.WorkflowGUI;
import view.form.LinkInputForm;

public class ResourceListView extends TitledPane implements IResourceListView{
	
	private ResourceList resourceList;
	
	private BorderPane containerPane;
	private ScrollPane scrollPane;
	private ListView<Resource> listPane;
	private FlowPane buttonPane;
	private Button addFile, addLink;
	
	public ResourceListView(ResourceList rl){
		resourceList = rl;
		generateUI();
		updateUI();
	}
	
	private void generateUI(){
		
		setText("Resources");
		
		containerPane = new BorderPane();
		
		listPane = new ListView<Resource>();
		listPane.setCellFactory((ListView<Resource> lv)->{
				ListCell<Resource> newCell = new ListCell<Resource>(){
					protected void updateItem(Resource resource, boolean empty){
						super.updateItem(resource, empty);
						
						if(resource == null || empty){
							setText(null);
							setGraphic(null);
						}else{
							if(resource.storedAsFile()){
								setGraphic(new FileResourceView((FileResource)resource));
							}else{
								setGraphic(new ResourceView(resource));
							}
						}
					}
				};
				return newCell;
			}
		);
		listPane.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		scrollPane = new ScrollPane();
		scrollPane.setMaxHeight(150);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setFitToWidth(true);
		scrollPane.setContent(listPane);
		
		addFile = new Button("Add File");
		addLink = new Button("Add Webpage");
		
		addFile.setOnAction((on_click)->{
				//open a filechooser so the user can add a new file to this task
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Add a File");
				File selection = fileChooser.showOpenDialog(WorkflowGUI.getStage());
				
				if(selection != null){
					resourceList.addResourceFromFile(resourceList.getParentTask(), selection.getAbsolutePath());
				}
			}
		);
		
		addLink.setOnAction((on_click)->{
				Dialog<ButtonType> linkDialog = new Dialog<ButtonType>();
				linkDialog.setTitle("Add a Webpage");
				
				LinkInputForm lif = new LinkInputForm();
				ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
				ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
				
				linkDialog.getDialogPane().setContent(lif);
				linkDialog.getDialogPane().getButtonTypes().addAll(ok, cancel);
				
				Optional<ButtonType> response = linkDialog.showAndWait();
				
				if(response.isPresent() && response.get().equals(ok)){
					resourceList.addResource(new WebResource(resourceList.getParentTask(), lif.getLink()));
				}
			}
		);
		
		buttonPane = new FlowPane();
		buttonPane.getChildren().addAll(addFile, addLink);
		
		containerPane.setCenter(scrollPane);
		containerPane.setBottom(buttonPane);
		
		setContent(containerPane);
		setExpanded(false);
	}
	
	public void updateUI(){
		listPane.getItems().clear();
		for(Resource r : resourceList.getModel()){
			listPane.getItems().add(r);
		}
		setText(resourceList.getModel().size() + " Resources");
	}
	
	public void updateStatus(boolean error){
		if(error){
			setGraphic(new ImageView(new Image(IResourceView.class.getResourceAsStream("../../resources/block_16.png"))));
			Tooltip.install(getGraphic(), new Tooltip("A resource in this list has a problem."));
		}else{
			setGraphic(null);
		}
	}
}
