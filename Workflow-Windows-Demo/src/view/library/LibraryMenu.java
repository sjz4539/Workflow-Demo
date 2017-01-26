package view.library;

import java.util.Optional;

import model.resource.task.FileResource;
import controller.Core;
import controller.library.Library;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LibraryMenu extends Menu implements ILibraryMenu{

	Library library;
	ImageView icon;
	MenuItem openItem, editNameItem, reloadItem, deleteItem;
	
	public LibraryMenu(Library l){
		library = l;
		generateUI();
		updateUI();
	}
	
	private void generateUI(){
		openItem = new MenuItem("Open");
		openItem.setOnAction((on_click)->{
			Core.getCore().setCurrentLibrary(library);
		});
		
		editNameItem = new MenuItem("Edit Name");
		editNameItem.setOnAction((on_click)->{
			TextInputDialog nameDialog = new TextInputDialog(library.getName());
			nameDialog.setTitle("Change Library Name");
			
			Optional<String> response = nameDialog.showAndWait();
			
			if(response.isPresent()){
				library.setName(response.get());
			}
		});
		reloadItem = new MenuItem("Reload");
		reloadItem.setOnAction((on_click)->{
			library.loadTasks();
		});
		deleteItem = new MenuItem("Delete");
		deleteItem.setOnAction((on_click)->{
			Core.getCore().removeLibrary(library);
		});
	}
	
	@Override
	public void updateUI() {
		setText(library.getName());
		getItems().clear();
		if(Core.getCore().getCurrentLibrary() == null || !Core.getCore().getCurrentLibrary().equals(library)){
			getItems().add(openItem);
		}
		getItems().addAll(editNameItem, reloadItem, deleteItem);
	}

	@Override
	public void updateStatus(String m) {
		if(library.getResource().getStatus() == FileResource.Status.ERROR || library.getResource().getStatus() == FileResource.Status.MISSING){
			setGraphic(getWarningIcon());
			Tooltip.install(getGraphic(), new Tooltip(m));
		}else{
			setGraphic(null);
		}
	}
	
	private ImageView getWarningIcon(){
		if(icon == null){
			icon = new ImageView(new Image(ResourceView.class.getResourceAsStream("../../resources/warning_16.png")));
		}
		return icon;
	}

}
