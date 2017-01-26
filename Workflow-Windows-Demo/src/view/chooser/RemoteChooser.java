package view.chooser;

import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import controller.io.FileOpsStatus;
import model.account.Account;
import model.resource.remote.RemoteFile;
import model.resource.remote.RemoteFolder;
import model.resource.remote.RemoteResource;

public class RemoteChooser extends Dialog<ButtonType>{
	
	private BorderPane dialogPane;
	private HBox topPane;

	private ListView<RemoteResource> listView;
	private TextField nameField;
	
	private RemoteFolder root, cur;
	private Chooser.Mode mode;

	private String filename, initialDirectory;
	private String[] extensions;
	
	public static final ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
	public static final ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
	public static final ButtonType open = new ButtonType("Open Folder", ButtonBar.ButtonData.NEXT_FORWARD);
	public static final ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
	
	public RemoteChooser(Chooser.Mode selectMode, Account account){
		this(selectMode, "", "", null, account);
	}
	
	public RemoteChooser(Chooser.Mode selectMode, String id, String fn, String[] ext, Account account){
		root = new RemoteFolder("", null, account);
		cur = root;
		
		mode = selectMode;
		
		initialDirectory = (id == null ? "" : id);
		filename = (fn == null ? "" : fn);
		extensions = (ext == null ? new String[]{} : ext);
		
		generateUI();
		updateUI();
	}
	
	private void generateUI(){
		
		Dialog<ButtonType> dialog = this;
		
		switch(mode){
			case MODE_SINGLE_FILE:
				setTitle("Select a file");
				break;
			case MODE_MULTIPLE_FILE:
				setTitle("Select one or more files");
				break;
			case MODE_SINGLE_FOLDER:
				setTitle("Select a folder");
				break;
			case MODE_SAVE_FILE:
				setTitle("Choose a location to save to");
				break;
			default:
				break;
		}
		
		topPane = new HBox(2);
		
		Button upButton = new Button();
		upButton.setGraphic(new ImageView(new Image(RemoteChooser.class.getResourceAsStream("../../resources/up_16.png"))));
		upButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				if(cur != root){
					cur = cur.getParent();
					updateUI();
				}
			}
		});
		Button newFolderButton = new Button();
		newFolderButton.setGraphic(new ImageView(new Image(RemoteChooser.class.getResourceAsStream("../../resources/plus_16.png"))));
		newFolderButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New Folder");
				dialog.setContentText("Enter a name for the new folder");
				Optional<String> name = dialog.showAndWait();
				if(name.isPresent() && name.get().length() > 0){
					if(cur.getSource().getFileOps().createFolder(cur.getSource(), cur, name.get()).getCode() == FileOpsStatus.Code.SUCCESS){
						cur.clear();
						updateUI();
					};
				}
			}
		});
		
		topPane.getChildren().addAll(upButton, newFolderButton);
		
		
		listView = new ListView<RemoteResource>();
		listView.setCellFactory(new Callback<ListView<RemoteResource>, ListCell<RemoteResource>>(){
			public ListCell<RemoteResource> call(ListView<RemoteResource> arg0) {
				
				ListCell<RemoteResource> newCell = new ListCell<RemoteResource>(){
					public void updateItem(RemoteResource res, boolean empty){
						super.updateItem(res, empty);
						if(empty){
							setGraphic(null);
							setText("");
						}else{
							setGraphic(new RemoteResourceView(res));
						}
					}
				};
				
				newCell.setOnMouseClicked(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent e) {
						if(e.getButton().equals(MouseButton.PRIMARY)){
							if(e.getClickCount() >= 2){
								if(newCell.getItem() != null && newCell.getItem().isDirectory()){
									cur = (RemoteFolder)newCell.getItem();
									updateUI();
								}else if(getSelection() != null){
									dialog.setResult(ButtonType.OK);
									dialog.close();
								}
							}
						}
					}
				});
				
				return newCell;
			}
		});

		listView.getSelectionModel().setSelectionMode(mode == Chooser.Mode.MODE_SINGLE_FILE || mode == Chooser.Mode.MODE_SINGLE_FOLDER ? SelectionMode.SINGLE : SelectionMode.MULTIPLE);
		
		nameField = new TextField(filename);
		
		dialogPane = new BorderPane();
		dialogPane.setPrefHeight(400);
		dialogPane.setTop(topPane);
		dialogPane.setCenter(listView);

		if(mode == Chooser.Mode.MODE_SAVE_FILE){
			dialogPane.setBottom(nameField);
		}

		setDialogPane(new DialogPane(){
			
			protected Node createButton(ButtonType type){
				
		        final Button button = new Button(type.getText());
		        final ButtonData buttonData = type.getButtonData();
		        ButtonBar.setButtonData(button, buttonData);
		        button.setDefaultButton(type != null && buttonData.isDefaultButton());
		        button.setCancelButton(type != null && buttonData.isCancelButton());
		        button.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent event) {
						if(type.equals(ok) || type.equals(save) || type.equals(cancel)){
							if (dialog != null) {
								dialog.setResult(type);
								dialog.close();
							}
						}else if(type.equals(open)){
							if(listView.getSelectionModel().getSelectedItem() != null && listView.getSelectionModel().getSelectedItem().isDirectory()){
								cur = (RemoteFolder)listView.getSelectionModel().getSelectedItem();
								updateUI();
							}
						}
					}
		        });
		        
		        return button;
		        
			}
			
		});
		
		getDialogPane().setContent(dialogPane);
		getDialogPane().getButtonTypes().clear();
		getDialogPane().getButtonTypes().addAll((mode == Chooser.Mode.MODE_SAVE_FILE ? save : ok), cancel);
		
		listView.getSelectionModel().getSelectedItems().addListener((Change<? extends RemoteResource> c)->{
				
			if(mode != Chooser.Mode.MODE_SINGLE_FOLDER){
				
				getDialogPane().getButtonTypes().clear();
				
				if(!listView.getSelectionModel().getSelectedItems().isEmpty() && listView.getSelectionModel().getSelectedItems().get(0).isDirectory()){
					getDialogPane().getButtonTypes().addAll(open, cancel);
				}else{
					getDialogPane().getButtonTypes().addAll((mode == Chooser.Mode.MODE_SAVE_FILE ? save : ok), cancel);
				}
			}
			
		});
		
		//init folder tree if necessary
		RemoteFolder search = cur;
		while(initialDirectory.length() > 0){
			
			if(initialDirectory.startsWith("/")){
				initialDirectory = initialDirectory.substring(initialDirectory.indexOf("/") + 1);
			}
			
			String nextFolderName;
			int endPos = initialDirectory.indexOf("/");
			nextFolderName = initialDirectory.substring(0, (endPos != -1 ? endPos : initialDirectory.length()));
			
			if(nextFolderName.length() > 0){
				RemoteFolder nextFolder = new RemoteFolder(nextFolderName, cur, cur.getSource());
				
				if(search.containsFolder(nextFolder)){
					search = nextFolder;
				}else{
					break;
				}
			}else{
				cur = search;
				root = cur;
			}
		}
		
	}

	public void updateUI(){
		listView.getItems().clear();
		//check the tree structure for data contained within the current active folder
		if(cur.getFolders() != null){
			for(RemoteFolder f : cur.getFolders()){
				listView.getItems().add(f);
			}
		}
		if(cur.getFiles() != null && mode != Chooser.Mode.MODE_SINGLE_FOLDER){
			for(RemoteFile f : cur.getFiles()){
				if(extensions.length == 0){
					listView.getItems().add(f);
				}else{
					for(String ext : extensions){
						if(f.getName().endsWith(ext)){
							listView.getItems().add(f);
						}
					}
				}
			}
		}
		listView.getSelectionModel().clearSelection();
	}
	
	public ArrayList<RemoteResource> getSelection(){
		ArrayList<RemoteResource> sel = new ArrayList<RemoteResource>();
		
		switch(mode){
		
			case MODE_SINGLE_FILE:
				if(listView.getSelectionModel().getSelectedItems().size() > 0 && !listView.getSelectionModel().getSelectedItems().get(0).isDirectory()){
					sel.add(listView.getSelectionModel().getSelectedItems().get(0));
				}
				break;
			case MODE_MULTIPLE_FILE:
				for(RemoteResource r : listView.getSelectionModel().getSelectedItems()){
					if(!r.isDirectory()){
						sel.add(r);
					}
				}
				break;
			case MODE_SINGLE_FOLDER:
				if(listView.getSelectionModel().getSelectedItems().size() > 0 && listView.getSelectionModel().getSelectedItems().get(0).isDirectory()){
					sel.add(listView.getSelectionModel().getSelectedItems().get(0));
				}
				break;
			case MODE_SAVE_FILE:
				sel.add(new RemoteFile(nameField.getText(), cur, cur.getSource()));
				break;
			default:
				break;
				
		}
		
		return sel;
	}
	
}
