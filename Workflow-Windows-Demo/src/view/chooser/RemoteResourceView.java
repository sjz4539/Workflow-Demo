package view.chooser;

import model.resource.remote.RemoteResource;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class RemoteResourceView extends HBox{

	private RemoteResource resource;

	private Label nameLabel;
	
	public RemoteResourceView(RemoteResource res){
		resource = res;
		generateUI();
	}
	
	private void generateUI(){
		nameLabel = new Label(resource.getName());
		nameLabel.setGraphic(new ImageView(new Image(resource.getIconResourceStream())));
		getChildren().add(nameLabel);
	}

}
