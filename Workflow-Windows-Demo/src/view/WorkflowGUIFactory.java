package view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.account.Account;
import model.resource.task.FileResource;
import model.resource.task.Resource;
import view.account.AccountListView;
import view.chooser.Chooser;
import view.chooser.ILocalChooserDialog;
import view.chooser.ILocalChooserPane;
import view.chooser.IRemoteChooserDialog;
import view.chooser.IRemoteChooserPane;
import view.chooser.LocalChooserDialog;
import view.chooser.LocalChooserPane;
import view.chooser.RemoteChooserDialog;
import view.chooser.RemoteChooserPane;
import view.chooser.Chooser.Mode;
import view.dialog.AccountDialog;
import view.dialog.CachedFilesDialog;
import view.dialog.LibraryDialog;
import view.dialog.UnsavedChangesDialog;
import view.form.NewTaskListForm;
import view.form.PropertiesForm;
import view.library.FileResourceView;
import view.library.IFileResourceView;
import view.library.ILibraryMenu;
import view.library.ILibraryView;
import view.library.IResourceListView;
import view.library.IResourceView;
import view.library.ITaskListView;
import view.library.ITaskView;
import view.library.LibraryMenu;
import view.library.LibraryView;
import view.library.ResourceListView;
import view.library.ResourceView;
import view.library.TaskListView;
import view.library.TaskView;
import view.oauth.IOauthHandlerView;
import view.oauth.OauthHandlerView;
import controller.Core;
import controller.library.Library;
import controller.library.RemovableLibrary;
import controller.library.ResourceList;
import controller.library.Task;
import controller.library.TaskList;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class WorkflowGUIFactory implements IWorkflowGUIFactory{

	public void displayError(String title, String header, String message, boolean modal){
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.ERROR);
			if(title != null){
				alert.setTitle(title);
			}
			if(header != null){
				alert.setHeaderText(header);
			}
			if(message != null){
				alert.setContentText(message);
			}
			if(modal){
				alert.showAndWait();
			}else{
				alert.show();
			}
		});
	}
	
	public void displayMessage(String title, String header, String message, boolean modal){
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.INFORMATION);
			if(title != null){
				alert.setTitle(title);
			}
			if(header != null){
				alert.setHeaderText(header);
			}
			if(message != null){
				alert.setContentText(message);
			}
			if(modal){
				alert.showAndWait();
			}else{
				alert.show();
			}
		});
	}
	
	public boolean requestConfirmation(String title, String header, String message){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		if(title != null){
			alert.setTitle(title);
		}
		if(header != null){
			alert.setHeaderText(header);
		}
		if(message != null){
			alert.setContentText(message);
		}
		
		Optional<ButtonType> response = alert.showAndWait();
		
		if(response.isPresent() && response.get().equals(ButtonType.OK)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean requestTextInput(String title, String header, String message, String value) {
		TextInputDialog dialog = new TextInputDialog(value);
		dialog.setTitle(title);
		dialog.setContentText(message);
		
		Optional<String> response = dialog.showAndWait();
		
		if(response.isPresent() && response.get().equals(ButtonType.OK)){
			value = dialog.getResult();
			return true;
		}else{
			return false;
		}
	}
	
	public void showConfigureDialog(){
		Platform.runLater(()->{
			PropertiesForm propForm = new PropertiesForm();
			Dialog<ButtonType> propDialog = new Dialog<ButtonType>();
			propDialog.getDialogPane().setContent(propForm);
			ButtonType ok = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
			ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			propDialog.getDialogPane().getButtonTypes().addAll(ok, cancel);
			
			Optional<ButtonType> confResult = propDialog.showAndWait();
			
			if(confResult.isPresent() && confResult.get().equals(ok)){
				propForm.saveProperties();
			}
		});
	}
	
	public void showNewLibraryDialog(){
		Platform.runLater(()->{
			LibraryDialog dialog = new LibraryDialog();
			Optional<ButtonType> response = dialog.showAndWait();
			if(response.isPresent() && response.get().equals(ButtonType.OK)){
				Core.getCore().addLibrary(dialog.makeLibrary(), true);
			}
		});
	}
	
	public void showImportLibraryDialog(){
		Platform.runLater(()->{
			LocalChooserDialog chooser = new LocalChooserDialog("", Chooser.Mode.MODE_SINGLE_FILE, false, "Import Library", "Select a library file", Library.REMOVABLE_LIBRARY_FILE_EXTENSION);
			if( chooser.showDialogAndWait() && !chooser.getString().isEmpty()){
				RemovableLibrary newLib = Core.getCore().loadRemovableLibraryData(chooser.getString());
				Core.getCore().addLibrary(newLib, true);
			}
		});
	}
	
	public void showNewAccountDialog(){
		Platform.runLater(()->{
			AccountDialog dialog = new AccountDialog();
			Optional<ButtonType> response = dialog.showAndWait();
			if(response.isPresent() && response.get().equals(ButtonType.OK)){
				Core.getCore().addAccount(dialog.makeAccount());
			}
		});
	}
	
	public void showAccountListDialog(){
		Platform.runLater(()->{
			AccountListView dialog = new AccountListView();
			dialog.showAndWait();
		});
	}
	
	@Override
	public void showNewTaskListDialog() {
		Platform.runLater(()->{
			Dialog<ButtonType> dialog = new Dialog<ButtonType>();
			dialog.setTitle("New Task List");
			NewTaskListForm ntlf = new NewTaskListForm();
			dialog.getDialogPane().setContent(ntlf);
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			
			Optional<ButtonType> response = dialog.showAndWait();
			if(response.isPresent() && response.get().equals(ButtonType.OK)){
				TaskList newList = ntlf.makeTaskList(Core.getCore().getCurrentLibrary());
				Core.getCore().getCurrentLibrary().addTaskList(newList);
			}
		});
	}
	
	@Override
	public boolean showUnsavedResourcesDialog() {
		UnsavedChangesDialog dialog = new UnsavedChangesDialog();
		Optional<ButtonType> response = dialog.showAndWait();
		return response.isPresent() && response.get().equals(ButtonType.OK);
	}

	@Override
	public boolean showCachedFilesDialog() {
		CachedFilesDialog dialog = new CachedFilesDialog();
		Optional<ButtonType> response = dialog.showAndWait();
		return response.isPresent() && response.get().equals(ButtonType.OK);
	}

	public List<File> showNativeFileChooser(Mode mode, String title, File initialFile, String... extensions) {
		return showNativeFileChooser(mode, title, initialFile.getName(), initialFile.getParent(), extensions);
	}

	public List<File> showNativeFileChooser(Mode mode, String title, String initialDirectory, String initialFilename, String... extensions) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title != null ? title : "");
		chooser.setInitialFileName(initialFilename != null ? initialFilename : "");
		chooser.setInitialDirectory(initialDirectory != null ? new File(initialDirectory) : null);
		
		if(extensions != null){
			for(String ext : extensions){
				chooser.getExtensionFilters().add(new ExtensionFilter(ext, ext));
			}
		}
		
		switch(mode){
			case MODE_SINGLE_FILE:
				File retFile = chooser.showOpenDialog(null);
				if(retFile != null){
					ArrayList<File> retFileList = new ArrayList<File>();
					retFileList.add(retFile);
					return retFileList;
				}else{
					return null;
				}
			case MODE_MULTIPLE_FILE:
				return chooser.showOpenMultipleDialog(null);
			case MODE_SINGLE_FOLDER:
				File retDir = chooser.showOpenDialog(null);
				if(retDir != null){
					ArrayList<File> retDirList = new ArrayList<File>();
					retDirList.add(retDir);
					return retDirList;
				}else{
					return null;
				}
			case MODE_SAVE_FILE:
				File saveFile = chooser.showOpenDialog(null);
				if(saveFile != null){
					ArrayList<File> saveFileList = new ArrayList<File>();
					saveFileList.add(saveFile);
					return saveFileList;
				}else{
					return null;
				}
			default:
				return null;
		}
	}
	
//==============================================================
	
	//===========================
	// GUI factory functions
	//===========================

	public ILibraryView getLibraryView(Library library) {
		return new LibraryView(library.getResource());
	}

	public ILibraryMenu getLibraryMenu(Library library) {
		return new LibraryMenu(library);
	}

	public ITaskListView getTaskListView(TaskList taskList) {
		return new TaskListView(taskList);
	}

	public ITaskView getTaskView(Task task) {
		return new TaskView(task);
	}

	public IResourceView getResourceView(Resource resource) {
		return new ResourceView(resource);
	}

	public IResourceListView getResourceListView(ResourceList resourceList) {
		return new ResourceListView(resourceList);
	}

	public IFileResourceView getFileResourceView(FileResource resource) {
		return new FileResourceView(resource);
	}

	public ILocalChooserPane getLocalChooserPane(String name, Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions) {
		return new LocalChooserPane(name, mode, editable, initialDirectory, initialFilename, extensions);
	}

	public ILocalChooserDialog getLocalChooserDialog(String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions) {
		return new LocalChooserDialog(name, mode, editable, title, message, initialDirectory, initialFilename, extensions);
	}

	public IRemoteChooserPane getRemoteChooserPane(Account account, String name, Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions) {
		return new RemoteChooserPane(account, name, mode, editable, initialDirectory, initialFilename, extensions);
	}

	public IRemoteChooserDialog getRemoteChooserDialog(Account account, String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions) {
		return new RemoteChooserDialog(account, name, mode, editable, title, message, initialDirectory, initialFilename, extensions);
	}

	public IOauthHandlerView getOauthDialog() {
		return new OauthHandlerView();
	}
	
}
