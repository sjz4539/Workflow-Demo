package view.chooser;

import java.io.File;
import java.util.List;
import java.util.Optional;

import controller.SimpleHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class LocalChooserDialog extends Dialog<ButtonType> implements ILocalChooserDialog{

	private LocalChooserPane pane;
	
	public LocalChooserDialog(String name, Mode mode, boolean editable, String title, String message, String... extensions) {
		this(name,mode, editable, title, message, null, null, extensions);
	}
	
	public LocalChooserDialog(String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions) {
		
		setTitle(title);
		pane = new LocalChooserPane(name, mode, editable, initialDirectory, initialFilename, extensions);
		
		BorderPane contentPane = new BorderPane();
		contentPane.setTop(new Label(message));
		contentPane.setCenter(pane);
		
		getDialogPane().setContent(contentPane);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
	}

	public void showDialog(SimpleHandler onAccept, SimpleHandler onCancel) {
	}

	public boolean showDialogAndWait() {
		Optional<ButtonType> response = this.showAndWait();
		return response.isPresent() && response.get().equals(ButtonType.OK);
	}

	public String getString(){
		return pane.getString();
	}

	public List<File> getSelection() {
		return pane.getSelection();
	}

}
