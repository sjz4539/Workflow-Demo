package view.dialog;

import java.util.ArrayList;
import java.util.Optional;

import view.library.FileResourceView;
import model.resource.task.FileResource;
import model.resource.task.Resource;
import controller.Core;
import controller.library.Library;
import controller.library.Task;
import controller.library.TaskList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class UnsavedChangesDialog extends Dialog<ButtonType>{
	
	private HBox libraryButtonPane, fileButtonPane, centerPane;
	private BorderPane libraryContentPane, fileContentPane, dialogContentPane;
	private Button selectAllFiles, selectAllLibraries, selectNoneFiles, selectNoneLibraries, saveSelected;
	private TitledPane libraryTitledPane, fileTitledPane;
	private ListView<UnsavedResource> libraryList, fileList;
	
	public static final ButtonType IGNORE = new ButtonType("Ignore", ButtonData.OTHER);
	
	public UnsavedChangesDialog(){
		generateUI();
		updateUI();
	}
	
	private void generateUI(){
		
		setTitle("Unsaved Resources");
		
		libraryTitledPane = new TitledPane();
		
		libraryList = new ListView<UnsavedResource>();
		libraryList.setCellFactory( (ListView<UnsavedResource> param)->{
			return new ListCell<UnsavedResource>(){
				public void updateItem(UnsavedResource resource, boolean empty){
					super.updateItem(resource, empty);
					if(empty){
						setGraphic(null);
					}else{
						setGraphic(resource.getView());
					}
				}
			};
		});
		
		ScrollPane libraryScroll = new ScrollPane(libraryList);
		libraryScroll.setFitToWidth(true);
		libraryScroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		libraryScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		libraryTitledPane.setContent(libraryScroll);
		libraryTitledPane.setGraphic(null);
		libraryTitledPane.prefHeightProperty().set(300);
		libraryTitledPane.setCollapsible(false);
		
		selectAllLibraries = new Button("Select All");
		selectAllLibraries.setOnAction((on_click)->{
			for(UnsavedResource r : libraryList.getItems()){
				r.getView().setSelected(true);
			}
		});
		selectNoneLibraries = new Button("Select None");
		selectNoneLibraries.setOnAction((on_click)->{
			for(UnsavedResource r : libraryList.getItems()){
				r.getView().setSelected(false);
			}
		});
		
		libraryButtonPane = new HBox(2);
		libraryButtonPane.getChildren().addAll(selectAllLibraries, selectNoneLibraries);
		
		libraryContentPane = new BorderPane();
		libraryContentPane.setTop(libraryButtonPane);
		libraryContentPane.setCenter(libraryTitledPane);
		
		
		fileTitledPane = new TitledPane();
		
		fileList = new ListView<UnsavedResource>();
		fileList.setCellFactory( (ListView<UnsavedResource> param)->{
			return new ListCell<UnsavedResource>(){
				public void updateItem(UnsavedResource resource, boolean empty){
					super.updateItem(resource, empty);
					if(empty){
						setGraphic(null);
					}else{
						setGraphic(resource.getView());
					}
				}
			};
		});
		
		ScrollPane fileScroll = new ScrollPane(fileList);
		fileScroll.setFitToWidth(true);
		fileScroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		fileScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		fileTitledPane.setContent(fileScroll);
		fileTitledPane.setGraphic(null);
		fileTitledPane.prefHeightProperty().set(300);
		fileTitledPane.setCollapsible(false);
		
		selectAllFiles = new Button("Select All");
		selectAllFiles.setOnAction((on_click)->{
			for(UnsavedResource r : fileList.getItems()){
				r.getView().setSelected(true);
			}
		});
		selectNoneFiles = new Button("Select None");
		selectNoneFiles.setOnAction((on_click)->{
			for(UnsavedResource r : fileList.getItems()){
				r.getView().setSelected(true);
			}
		});
		
		fileButtonPane = new HBox(2);
		fileButtonPane.getChildren().addAll(selectAllFiles, selectNoneFiles);
		
		fileContentPane = new BorderPane();
		fileContentPane.setTop(fileButtonPane);
		fileContentPane.setCenter(fileTitledPane);
		
		saveSelected = new Button("Save Selected Resources");
		saveSelected.setOnAction((on_click)->{
			for(UnsavedResource r : libraryList.getItems()){
				if(r.getView().isSelected()){
					r.getResource().save();
				}
			}
			for(UnsavedResource r : fileList.getItems()){
				if(r.getView().isSelected()){
					r.getResource().save();
				}
			}
		});
		
		centerPane = new HBox(2);
		centerPane.getChildren().addAll(libraryContentPane, fileContentPane);
		
		dialogContentPane = new BorderPane();
		BorderPane.setMargin(centerPane, new Insets(5,0,5,0));
		dialogContentPane.setTop(new Label("Some cached resources have local changes that have not been uploaded to the libraries they\nare stored in. Please save any changes you want to keep before continuing."));
		dialogContentPane.setCenter(centerPane);
		dialogContentPane.setBottom(saveSelected);
		BorderPane.setAlignment(saveSelected, Pos.CENTER);
		
		getDialogPane().setContent(dialogContentPane);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
	}
	
	public void updateUI(){
		ArrayList<Library> unsavedLibraries = new ArrayList<Library>();
		ArrayList<FileResource> unsavedFiles = new ArrayList<FileResource>();

		for(Library l : Core.getCore().getLibraries()){
			if(l.getResource().isLoaded()){
				if(l.getResource().needsSave()){
					unsavedLibraries.add(l);
				}
				for(TaskList tl : l.getLibraryData()){
					for(Task t : tl.getModel()){
						for(Resource r : t.getResourceList().getModel()){
							if(r.isLoaded() && r.storedAsFile() && ((FileResource)r).needsSave()){
								unsavedFiles.add((FileResource)r);
							}
						}
					}
				}
			}
		}
		
		if(unsavedLibraries.size() > 0){
			libraryList.getItems().clear();
			for(Library l : unsavedLibraries){
				libraryList.getItems().add(new UnsavedResource(l.getResource()));
			}
		}
		
		if(unsavedFiles.size() > 0){
			fileList.getItems().clear();
			for(FileResource r : unsavedFiles){
				fileList.getItems().add(new UnsavedResource(r));
			}
		}
		
		updateHeaders();
	}
	
	public void updateHeaders(){
		int librariesChecked = 0, filesChecked = 0;
		
		for(UnsavedResource ur : libraryList.getItems()){
			if(ur.getView().isSelected()){
				librariesChecked++;
			}
		}
		for(UnsavedResource ur : fileList.getItems()){
			if(ur.getView().isSelected()){
				filesChecked++;
			}
		}
		
		libraryTitledPane.setText(libraryList.getItems().size() + " Libraries (" + librariesChecked + " selected)");
		fileTitledPane.setText(fileList.getItems().size() + " Files (" + filesChecked + " selected)");
	}

	public Optional<ButtonType> maybeShowAndWait(){
		if(libraryList.getItems().isEmpty() && fileList.getItems().isEmpty()){
			return Optional.of(IGNORE);
		}else{
			return super.showAndWait();
		}
	}
	
//====================================================================
	
	private class UnsavedResource{
		
		private FileResource resource;
		private UnsavedResourceView view;
		
		public UnsavedResource(FileResource r){
			resource = r;
		}
		
		public Resource getResource(){
			return resource;
		}
		
		public UnsavedResourceView getView(){
			if(view == null){
				view = new UnsavedResourceView(resource);
			}
			return view;
		}
		
	}
	
	private class UnsavedResourceView extends BorderPane{
	
		protected CheckBox checkBox;
		
		public UnsavedResourceView(FileResource resource){
			checkBox = new CheckBox();
			FileResourceView view = new FileResourceView(resource);

			setAlignment(checkBox, Pos.CENTER);
			setAlignment(view, Pos.CENTER);
			
			setLeft(checkBox);
			setCenter(view);
		}

		public boolean isSelected(){
			return checkBox.isSelected();
		}
		
		public void setSelected(boolean selected){
			checkBox.setSelected(selected);
		}
		
	}
}
