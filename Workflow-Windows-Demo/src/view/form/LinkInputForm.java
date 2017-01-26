package view.form;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class LinkInputForm extends HBox implements ILinkInputForm{

	private TextField linkField;
	
	public LinkInputForm(){
		linkField = new TextField();
		getChildren().addAll(new Label("URL:"), linkField);
	}
	
	public LinkInputForm(String l){
		linkField = new TextField(l);
		getChildren().addAll(new Label("URL:"), linkField);
	}
	
	public String getLink(){
		return linkField.getText();
	}
	
}
