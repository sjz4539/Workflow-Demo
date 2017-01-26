package view.dialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import controller.Core;
import controller.library.*;
import model.account.Account;
import view.chooser.LocalChooserPane;
import view.chooser.RemoteChooserPane;
import view.chooser.Chooser;

public class LibraryDialog extends Dialog<ButtonType>{

	private LibraryForm.LocalLibraryForm localForm;
	private LibraryForm.RemovableLibraryForm removableForm;
	private LibraryForm.RemoteLibraryForm remoteForm;
	private ComboBox<String> libType;
	
	private LocalLibrary local_library;
	private RemovableLibrary removable_library;
	private RemoteLibrary remote_library;
	
	private Library library;
	
	final String libOptDropbox = "Dropbox";
	final String libOptGoogle = "Google Drive";
	final String libOptRemovable = "Removable";
	final String libOptLocal = "Local";
	
	public LibraryDialog(){
		generateUI();
		updateUI();
	}
	
	public LibraryDialog(LocalLibrary l){
		local_library = l;
		library = l;
		generateUI();
		updateUI();
	}
	
	public LibraryDialog(RemovableLibrary l){
		removable_library = l;
		library = l;
		generateUI();
		updateUI();
	}
	
	public LibraryDialog(RemoteLibrary l){
		remote_library = l;
		library = l;
		generateUI();
		updateUI();
	}
	
	private void generateUI(){
		
		localForm = (local_library != null ? new LibraryForm.LocalLibraryForm(local_library) : new LibraryForm.LocalLibraryForm());
		removableForm = (removable_library != null ? new LibraryForm.RemovableLibraryForm(removable_library) : new LibraryForm.RemovableLibraryForm());
		remoteForm = (remote_library != null ? new LibraryForm.RemoteLibraryForm(remote_library) : new LibraryForm.RemoteLibraryForm());

		libType = new ComboBox<String>();
		libType.setDisable(library != null);
		libType.getItems().addAll(libOptDropbox, libOptGoogle, libOptRemovable, libOptLocal);
		
		libType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				switch(newValue){
					case libOptDropbox:
						remoteForm.setVisible(true);
						remoteForm.setAccountType(Account.AccountType.ACCOUNT_TYPE_DROPBOX);
						removableForm.setVisible(false);
						localForm.setVisible(false);
						break;
					case libOptGoogle:
						remoteForm.setVisible(true);
						remoteForm.setAccountType(Account.AccountType.ACCOUNT_TYPE_GOOGLE);
						removableForm.setVisible(false);
						localForm.setVisible(false);
						break;
					case libOptRemovable:
						remoteForm.setVisible(false);
						removableForm.setVisible(true);
						localForm.setVisible(false);
						break;
					case libOptLocal:
						remoteForm.setVisible(false);
						removableForm.setVisible(false);
						localForm.setVisible(true);
						break;
					default:
						remoteForm.setVisible(false);
						removableForm.setVisible(false);
						localForm.setVisible(true);
						break;
				}
			}
		});
		
		HBox topPane = new HBox(2, new Label("Library Type:"), libType);
		
		StackPane centerPane = new StackPane(remoteForm, removableForm, localForm);
		centerPane.setPadding(new Insets(5, 0, 0, 0));
		
		BorderPane content = new BorderPane();
		content.setTop(topPane);
		content.setCenter(centerPane);
		
		getDialogPane().setContent(content);
		setTitle("Create/Edit Library");
		setHeaderText("Select a library type, enter the required information, and click ok.");
		
		getDialogPane().getButtonTypes().clear();
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
	}
	
	private void updateUI(){
		if(remote_library != null && remote_library.getAccount() != null){
			if(remote_library.getAccount().getType() == Account.AccountType.ACCOUNT_TYPE_GOOGLE){
				libType.getSelectionModel().select(libOptGoogle);
				remoteForm.setVisible(true);
				removableForm.setVisible(false);
				localForm.setVisible(false);
			}else if(remote_library.getAccount().getType() == Account.AccountType.ACCOUNT_TYPE_DROPBOX){
				libType.getSelectionModel().select(libOptDropbox);
				remoteForm.setVisible(true);
				removableForm.setVisible(false);
				localForm.setVisible(false);
			}
		}else if(removable_library != null){
			libType.getSelectionModel().select(libOptRemovable);
			remoteForm.setVisible(false);
			removableForm.setVisible(true);
			localForm.setVisible(false);
		}else{
			libType.getSelectionModel().select(libOptLocal);
			remoteForm.setVisible(false);
			removableForm.setVisible(false);
			localForm.setVisible(true);
		}
	}
	
	public Library makeLibrary(){
		
		Library newLib = null;
		
		switch(libType.getValue()){
			
			case libOptDropbox:
				newLib = remoteForm.makeLibrary();
				break;
			case libOptGoogle:
				newLib = remoteForm.makeLibrary();
				break;
			case libOptRemovable:
				newLib = removableForm.makeLibrary();
				break;
			case libOptLocal:
				newLib = localForm.makeLibrary();
				break;
			default:
				break;
		}
		
		return newLib;
	}
	
	private static abstract class LibraryForm extends VBox{
		
		protected abstract Library makeLibrary();
		
		private static class LocalLibraryForm extends LibraryForm{
			
			private TextField local_libraryname;
			private LocalChooserPane local_libraryroot;
			private TextField local_librarypath;
			private LocalLibrary library;
			
			public LocalLibraryForm(){
				this(null);
			}
			
			public LocalLibraryForm(LocalLibrary l){
				library = l;
				generateUI();
				updateUI();
			}
			
			private void generateUI(){
				
				this.setSpacing(2);
				
				local_libraryname = new TextField();
				if(library != null){
					local_libraryroot = new LocalChooserPane("Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false, library.getRoot(), null);
				}else{
					local_libraryroot = new LocalChooserPane("Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false);
				}
				local_librarypath = new TextField();
				getChildren().addAll(
						new HBox( 2, new Label("Library Name:"), local_libraryname),
						local_libraryroot,
						new HBox( 2, new Label("Library Path:"), local_librarypath)
				);
			}
			
			public void updateUI(){
				local_libraryname.setText(library != null ? library.getName() : "Workflow Library");
				local_librarypath.setText(library != null ? library.getResource().getPath() : Library.DEFAULT_LIBRARY_FILENAME + Library.RESOURCE_FILE_EXTENSION);
			}
	
			public LocalLibrary makeLibrary() {
				if(library == null){
					return new LocalLibrary(
						local_libraryname.getText(), 
						(local_libraryroot.getSelection().size() > 0 ? local_libraryroot.getSelection().get(0).getAbsolutePath() : ""), 
						local_librarypath.getText()
					);
				}else{
					return library;
				}
			}
			
		}
		
		private static class RemovableLibraryForm extends LibraryForm{
			
			private TextField removable_libraryname;
			private LocalChooserPane removable_libraryroot;
			private TextField removable_librarypath;
			private RemovableLibrary library;
			
			public RemovableLibraryForm(){
				this(null);
			}
			
			public RemovableLibraryForm(RemovableLibrary l){
				library = l;
				generateUI();
				updateUI();
			}
			
			private void generateUI(){
				
				this.setSpacing(2);
				
				removable_libraryname = new TextField();
				if(library != null){
					removable_libraryroot = new LocalChooserPane("Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false, library.getRoot(), null);
				}else{
					removable_libraryroot = new LocalChooserPane("Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false);
				}
				removable_librarypath = new TextField();
				getChildren().addAll(
						new HBox( 2, new Label("Library Name:"), removable_libraryname),
						removable_libraryroot,
						new HBox( 2, new Label("Library Path:"), removable_librarypath)
				);
			}
			
			public void updateUI(){
				removable_libraryname.setText(library != null ? library.getName() : "Workflow Library");
				removable_librarypath.setText(library != null ? library.getResource().getPath() : Library.DEFAULT_LIBRARY_FILENAME + Library.REMOVABLE_LIBRARY_FILE_EXTENSION);
			}
	
			public RemovableLibrary makeLibrary() {
				if(library == null){
					return new RemovableLibrary(
							removable_libraryname.getText(), 
						(removable_libraryroot.getSelection().size() > 0 ? removable_libraryroot.getSelection().get(0).getAbsolutePath() : ""), 
						removable_librarypath.getText()
					);
				}else{
					return library;
				}
			}
			
		}
		
		private static class RemoteLibraryForm extends LibraryForm{
			
			private Label remote_accountlabel;
			private ComboBox<Account> remote_accountlist;
			private Button remote_newaccountbutton;
			private TextField remote_libraryname;
			private RemoteChooserPane remote_libraryroot;
			private TextField remote_librarypath;
			private RemoteLibrary library;
			
			private Account.AccountType accountType;
			
			public RemoteLibraryForm(){
				this(null);
			}
			
			public RemoteLibraryForm(RemoteLibrary dl){
				library = dl;
				generateUI();
				updateUI();
			}
			
			private void generateUI(){
				
				this.setSpacing(2);
				
				remote_libraryname = new TextField();
				
				remote_accountlabel = new Label();
				remote_accountlist = new ComboBox<Account>();
				remote_accountlist.setDisable(library != null); //disable changing the account for existing libraries
				remote_accountlist.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>(){
					public ListCell<Account> call(ListView<Account> param) {
						ListCell<Account> newCell = new ListCell<Account>(){
							
							public void updateItem(Account item, boolean empty){
								super.updateItem(item, empty);
								setGraphic(null);
								setText(empty ? "" : item.getName());
							}
							
						};
						
						return newCell;
					}
				});
				remote_accountlist.setConverter(new StringConverter<Account>(){
					public String toString(Account object) {
						return object.getName();
					}
					public Account fromString(String string) {
						return null;
					}
				});
				remote_accountlist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>(){
					public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
						remote_libraryroot.setAccount(newValue);
					}
				});
				
				remote_newaccountbutton = new Button("New Account");
				remote_newaccountbutton.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent event) {
						Core.getGuiFactory().showNewAccountDialog();
						updateUI();
					}
				});
				
				remote_libraryroot = (library != null ? 
					new RemoteChooserPane(library.getAccount(), "Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false, library.getRoot(), null) :
					new RemoteChooserPane(null, "Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false, null, null)
				);
				remote_libraryroot.setEnabled(library == null);
				
				remote_librarypath = new TextField();
				remote_librarypath.setDisable(library != null);
				
				getChildren().addAll(
						new HBox( 2, remote_accountlabel, remote_accountlist, remote_newaccountbutton),
						new HBox( 2, new Label("Library Name:"), remote_libraryname),
						remote_libraryroot,
						new HBox( 2, new Label("Library Path:"), remote_librarypath)
				);
			}
			
			public void updateUI(){
				if(accountType != null){
					switch(accountType){
						case ACCOUNT_TYPE_DROPBOX:
							remote_accountlabel.setText("Dropbox Account:");
							break;
						case ACCOUNT_TYPE_GOOGLE:
							remote_accountlabel.setText("Google Drive Account:");
							break;
						default:
							remote_accountlabel.setText("Remote Storage Account:");
							break;
					}
					remote_accountlist.getItems().clear();
					for(Account account : Core.getCore().getAccounts()){
						if(account.getType() == accountType){
							remote_accountlist.getItems().add(account);
						}
					}
				}
				if(library != null && remote_accountlist.getItems().contains(library.getAccount())){
					remote_accountlist.getSelectionModel().select(library.getAccount());
				}
				remote_libraryname.setText(library != null ? library.getName() : "Workflow Library");
				remote_librarypath.setText(library != null ? library.getResource().getPath() : Library.DEFAULT_LIBRARY_FILENAME + Library.RESOURCE_FILE_EXTENSION);
			}
			
			public void setAccountType(Account.AccountType type){
				accountType = type;
				updateUI();
			}
	
			public RemoteLibrary makeLibrary() {
				if(library == null){
					return new RemoteLibrary(
						remote_libraryname.getText(),
						remote_libraryroot.getString(),
						remote_librarypath.getText(),
						remote_accountlist.getSelectionModel().getSelectedItem()
					);
				}else{
					library.setName(remote_libraryname.getText());
					return library;
				}
			}
		}
	}
}
