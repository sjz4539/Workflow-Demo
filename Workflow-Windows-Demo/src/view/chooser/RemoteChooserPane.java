package view.chooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import model.account.Account;
import model.resource.remote.RemoteResource;

public class RemoteChooserPane extends HBox implements IRemoteChooserPane{

	private TextField textField;
	private Button button;
	private RemoteChooser dialog;
	
	private Account account;
	
	public RemoteChooserPane(Account a, String name, Chooser.Mode mode, boolean editable){
		this(a, name, mode, editable, null, null, (String[])null);
	}
	
	public RemoteChooserPane(Account a, String name, Chooser.Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions){

		setSpacing(2);
		setAccount(a);
		
		textField = new TextField( 
			initialDirectory != null && initialFilename != null ? (
				(
					initialDirectory.endsWith(File.separator) || initialDirectory.equals(File.separator) ? 
					initialDirectory : 
					initialDirectory + File.separator
				) + (
					initialFilename.startsWith(File.separator) ?
					initialFilename.substring(1) : 
					initialFilename
				)
			) : ""
		);
		textField.setEditable(editable);
		
		button = new Button("Browse...");
		button.setOnAction((on_click)->{
				
			if(account != null){
				 
				Optional<ButtonType> response;
				
				switch(mode){
					case MODE_SAVE_FILE:
						
						dialog = new RemoteChooser(mode, initialDirectory, initialFilename, extensions, account);
						response = dialog.showAndWait();
						if(response.isPresent() && response.get().equals(RemoteChooser.save)){
							if(dialog.getSelection().size() > 0){
								textField.setText(dialog.getSelection().get(0).getPath());
							}
						}
						
						break;
					case MODE_SINGLE_FILE:

						dialog = new RemoteChooser(mode, initialDirectory, initialFilename, extensions, account);
						response = dialog.showAndWait();
						if(response.isPresent() && response.get().equals(RemoteChooser.ok)){
							if(dialog.getSelection().size() > 0){
								textField.setText(dialog.getSelection().get(0).getPath());
							}
						}
								
						break;
					case MODE_MULTIPLE_FILE:
						
						dialog = new RemoteChooser(mode, initialDirectory, "", extensions, account);
						response = dialog.showAndWait();
						if(response.isPresent() && response.get().equals(RemoteChooser.ok)){
							String str = "", part = "";
							for(int i = 0; i < dialog.getSelection().size(); i++){
								part = dialog.getSelection().get(i).getPath();
								str += part;
								str += (i < dialog.getSelection().size() - 1 ? "," : "");
							}
							textField.setText(str);
						}
						
						break;
					case MODE_SINGLE_FOLDER:
						
						dialog = new RemoteChooser(mode, initialDirectory, "", null, account);
						response = dialog.showAndWait();
						if(response.isPresent() && response.get().equals(RemoteChooser.ok)){
							if(dialog.getSelection().size() > 0){
								textField.setText(dialog.getSelection().get(0).getPath());
							}
						}
						
						break;
					default:
						break;
				}
			}
		});
		
		getChildren().addAll(
				new Label(name),
				textField,
				button
		);
		
		setEnabled(account != null);
	}
	
	public String getString(){
		return textField.getText();
	}
	
	public List<RemoteResource> getSelection(){
		return dialog != null ? dialog.getSelection() : new ArrayList<RemoteResource>();
	}
	
	public void setAccount(Account a){
		account = a;
		setEnabled(account != null);
	}
	
	public void setEnabled(boolean enable){
		if(textField != null && button != null){
			textField.setEditable(enable);
			button.setDisable(!enable);
		}
	}

}
