package view.library;

import java.util.ArrayList;
import java.util.List;
import model.resource.task.Resource;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public class ResourceView extends BorderPane implements IResourceView{

	protected Label nameLabel;
	protected MenuButton menuButton;
	protected MenuItem openItem, copyItem, removeItem;
	protected Resource resource;
	
	protected ResourceView(){
	}
	
	public ResourceView(Resource r){
		resource = r;
		generateUI();
		updateUI();
	}
	
	protected void generateUI(){
		
		nameLabel = new Label(resource.getName());
		menuButton = new MenuButton("...");
		menuButton.getItems().addAll(getMenuItems());
		
		setAlignment(nameLabel, Pos.CENTER_LEFT);
		setAlignment(menuButton, Pos.CENTER_RIGHT);
		
		setCenter(nameLabel);
		setRight(menuButton);
		
	}
	
	public void updateUI(){
		nameLabel.setText(resource.getName());
	}
	
	public void setResource(Resource r){
		resource = r;
	}
	
	public List<MenuItem> getMenuItems(){

		ArrayList<MenuItem> items = new ArrayList<MenuItem>();
			
		openItem = new MenuItem("Open");
		openItem.setOnAction((click)->{
			resource.open();
		});
		items.add(openItem);
		
		copyItem = new MenuItem("Copy");
		copyItem.setOnAction((click)->{
			
		});
		items.add(copyItem);
		
		removeItem = new MenuItem("Remove");
		removeItem.setOnAction((click)->{
			resource.getParentTask().getResourceList().removeResource(resource);
		});
		items.add(removeItem);
			
		return items;
	}
	
}
