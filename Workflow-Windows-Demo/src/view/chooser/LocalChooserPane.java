package view.chooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import view.WorkflowGUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class LocalChooserPane extends HBox implements ILocalChooserPane{

	private TextField textField;
	private ArrayList<File> selection = new ArrayList<File>();
	private Button button;
	
	public LocalChooserPane(String name, Chooser.Mode mode, boolean editable){
		this(name, mode, editable, null, null, (String[])null);
	}
	
	public LocalChooserPane(String name, Chooser.Mode mode, boolean editable, String initialDirectory, String initialFilename, String... filters){
		
		setSpacing(2);
		
		textField = new TextField( 
			initialDirectory != null && initialFilename != null ? (
				(
					initialDirectory.endsWith(File.separator) && !initialDirectory.equals(File.separator) ? 
					initialDirectory.substring(0, initialDirectory.length() - 1) : 
					initialDirectory
				) + (
					initialFilename.startsWith(File.separator) ?
					initialFilename.substring(1) : 
					initialFilename
				)
			) : ""
		);
		textField.setEditable(editable);
		
		button = new Button("Browse...");
		button.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				File result;
				List<File> resultList; 
				switch(mode){
					case MODE_SAVE_FILE:
						FileChooser saveFileChooser = new FileChooser();
						saveFileChooser.setInitialDirectory((initialDirectory != null && initialDirectory.length() > 0 ? new File(initialDirectory) : null));
						saveFileChooser.setInitialFileName((initialFilename != null && initialFilename.length() > 0 ? initialFilename : ""));

						result = saveFileChooser.showSaveDialog(WorkflowGUI.getStage());
						if(result != null){
							selection.clear();
							selection.add(result);
							textField.setText(selection.size() > 0 ? selection.get(0).getAbsolutePath() : "");
						}
						break;
					case MODE_SINGLE_FILE:
						FileChooser singleFileChooser = new FileChooser();
						singleFileChooser.setInitialDirectory((initialDirectory != null && initialDirectory.length() > 0 ? new File(initialDirectory) : null));
						singleFileChooser.setInitialFileName((initialFilename != null && initialFilename.length() > 0 ? initialFilename : ""));
						if(filters != null){
							ExtensionFilter[] filArr = new ExtensionFilter[filters.length];
							for(int i = 0; i < filters.length; i++){
								filArr[i] = new ExtensionFilter(filters[i], filters[i]);
							}
							singleFileChooser.getExtensionFilters().addAll(filArr);
						}
						result = singleFileChooser.showOpenDialog(WorkflowGUI.getStage());
						if(result != null){
							selection.clear();
							selection.add(result);
							textField.setText(selection.size() > 0 ? selection.get(0).getAbsolutePath() : "");
						}
						break;
					case MODE_MULTIPLE_FILE:
						FileChooser multipleFileChooser = new FileChooser();
						multipleFileChooser.setInitialDirectory((initialDirectory != null && initialDirectory.length() > 0 ? new File(initialDirectory) : null));
						resultList = multipleFileChooser.showOpenMultipleDialog(WorkflowGUI.getStage());
						if(filters != null){
							ExtensionFilter[] filArr = new ExtensionFilter[filters.length];
							for(int i = 0; i < filters.length; i++){
								filArr[i] = new ExtensionFilter(filters[i], filters[i]);
							}
							multipleFileChooser.getExtensionFilters().addAll(filArr);
						}
						if(resultList != null){
							selection.clear();
							selection.addAll(resultList);
							
							String str = "";
							for(int i = 0; i < selection.size(); i++){
								str += selection.get(i).getAbsolutePath();
								str += (i < selection.size() - 1 ? "," : "");
							}
							textField.setText(str);
						}
						break;
					case MODE_SINGLE_FOLDER:
						DirectoryChooser singleDirectoryChooser = new DirectoryChooser();
						singleDirectoryChooser.setInitialDirectory((initialDirectory != null && initialDirectory.length() > 0 ? new File(initialDirectory) : null));
						result = singleDirectoryChooser.showDialog(WorkflowGUI.getStage());
						if(result != null){
							selection.clear();
							selection.add(result);
							textField.setText(selection.size() > 0 ? selection.get(0).getAbsolutePath() : "");
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
	}
	
	public String getString(){
		return textField.getText();
	}
	
	public ArrayList<File> getSelection(){
		return selection;
	}
	
}
