package view.dialog;

import controller.Core;
import model.resource.task.FileResource;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class CachedFilesDialog extends Dialog<ButtonType>{

	private ListView<FileResource> resourceList;
	
	public static ButtonType OK = new ButtonType("OK", ButtonData.OTHER), 
							 IGNORE = new ButtonType("Ignore", ButtonData.OTHER), 
							 CANCEL = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	
	public CachedFilesDialog(){
		generateUI();
		updateUI();
	}
	
	private void generateUI(){
		resourceList = new ListView<FileResource>();
		resourceList.setCellFactory((listview)->{
			return new ListCell<FileResource>(){
				public void updateItem(FileResource resource, boolean empty){
					super.updateItem(resource, empty);
					if(!empty && resource != null){
						setText(resource.getName() + " (" + Core.getResourceCache().getAbsolutePath(resource) + ")");
						setGraphic(new ImageView(new Image(CachedFilesDialog.class.getResourceAsStream("../../resources/document_16.png"))));
					}else{
						setText(null);
						setGraphic(null);
					}
				}
			};
		});
		
		setHeaderText("The following resources could not be cleared from the cache. Please close any external programs that may be using them and click OK:");
		getDialogPane().setContent(new ScrollPane(resourceList));
		
		getDialogPane().getButtonTypes().clear();
		getDialogPane().getButtonTypes().addAll(IGNORE, OK, CANCEL);
	}
	
	public void updateUI(){
		resourceList.getItems().clear();
		for(FileResource resource : Core.getResourceCache().getContents()){
			resourceList.getItems().add(resource);
		}
	}
	
}
