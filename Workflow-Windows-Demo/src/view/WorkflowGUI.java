package view;

import view.library.ILibraryView;
import view.library.LibraryView;
import view.library.LibraryMenu;
import controller.Core;
import controller.CoreExtension;
import controller.ProcessMonitor;
import controller.library.Library;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class WorkflowGUI extends Application implements IWorkflowGUI{
	
	//root pane for GUI, singleton
	private static BorderPane root;
	private static Pane emptyView;
	private static Menu libraryMenu;
	private static MenuItem newLibrary, importLibrary;
	private static Stage stage;
	
	public static void main(String[] args){
		launch(args);
	}
	
	//Primary program method
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Workflow");
		
		primaryStage.setOnHiding((closeRequest)->{
			//program is closing, shut down all threads
			//if threads won't stop or some error occurs, prevent window close
			if(!Core.getCore().close()){
				if(!Core.getGuiFactory().requestConfirmation(null, null, "One or more errors occurred while attempting to close the program. Are you sure you want to quit?")){
					closeRequest.consume();
				}
			}
		});
		
		stage = primaryStage;
		
		//initialize the root pane before doing anything.
		getRoot();
		
		//set core up
		Core.getCore().setGui(this);
		Core.getCore().setGuiFactory(new WorkflowGUIFactory());
		Core.getCore().setCoreExtension(new CoreExtension());
		Core.getCore().setProcessMonitor(new ProcessMonitor());
		
		//do core init
		if(!Core.getCore().init()){
			//display some error dialog box
		}else{
			
			//Core should be good to go, set up the gui
			getRoot().setTop(getMenuBar());
			getRoot().setCenter(getEmptyView());
			
			primaryStage.setScene(new Scene(root, 500, 500));
			primaryStage.minWidthProperty().set(100);
			primaryStage.minHeightProperty().set(100);
			
			setUserAgentStylesheet(STYLESHEET_CASPIAN);
			
			primaryStage.show();
		}
	}

	public static Stage getStage(){
		return stage;
	}
	
	//Static accessor for root pane instance
	public static BorderPane getRoot(){
		if(root == null){
			root = new BorderPane();
		}
		return root;
	}
	
	public void updateLibraryList(){

		libraryMenu.getItems().clear();
		
		for(Library l : Core.getCore().getLibraries()){
			Menu nextItem = (LibraryMenu)l.getMenu();
			libraryMenu.getItems().add(nextItem);
		}
		
		if(newLibrary == null){
			newLibrary = new MenuItem("New Library");
			newLibrary.setOnAction((click)->{
				Core.getGuiFactory().showNewLibraryDialog();
			});
		}
		
		if(importLibrary == null){
			importLibrary = new MenuItem("Import Library ");
			importLibrary.setOnAction((click)->{
				Core.getGuiFactory().showImportLibraryDialog();
			});
		}
		
		libraryMenu.getItems().addAll(newLibrary, importLibrary);
		
	}
	
	public void setLibraryView(ILibraryView lrv){
		Platform.runLater(()->{
			getRoot().setCenter( lrv == null ? getEmptyView() : (LibraryView)lrv);
		});
	}
	

	
//========================================================
	 
	//===========================
	//private GUI setup functions
	//===========================
	
	private static Pane getEmptyView(){
		if(emptyView == null){
			emptyView = new Pane();
		}
		return emptyView;
	}
	
	private MenuBar getMenuBar(){

		//todo, needs to provide access to core program functions
		//	-create task list
		//	-settings
		//	-exit
		
		//feels like there must be more than that; list needs to be expanded
		MenuBar menubar = new MenuBar();
		
		//Main menu - general program functions
		Menu mainMenu = new Menu("Main");
		MenuItem configureItem = new MenuItem("Configure");
		configureItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				Core.getGuiFactory().showConfigureDialog();
			}
		});
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				WorkflowGUI.getStage().close();
			}
		});
		mainMenu.getItems().addAll(configureItem, exitItem);
		
		Menu accountMenu = new Menu("Accounts");
		MenuItem newAccountItem = new MenuItem("New Account");
		newAccountItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				Core.getGuiFactory().showNewAccountDialog();
			}
		});
		MenuItem accountListItem = new MenuItem("View/Edit Accounts");
		accountListItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				Core.getGuiFactory().showAccountListDialog();
			}
		});
		accountMenu.getItems().addAll(newAccountItem, accountListItem);
		
		//Library menu - library management functions
		libraryMenu = new Menu("Library");
		
		menubar.getMenus().addAll(mainMenu, accountMenu, libraryMenu);
		
		updateLibraryList();
		
		return menubar;
	}

}
