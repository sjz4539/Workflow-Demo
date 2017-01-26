package view;

import java.io.File;
import java.util.List;

import controller.library.Library;
import controller.library.ResourceList;
import controller.library.Task;
import controller.library.TaskList;
import model.account.Account;
import model.resource.task.FileResource;
import model.resource.task.Resource;
import view.chooser.Chooser;
import view.chooser.ILocalChooserDialog;
import view.chooser.ILocalChooserPane;
import view.chooser.IRemoteChooserDialog;
import view.chooser.IRemoteChooserPane;
import view.library.IFileResourceView;
import view.library.ILibraryMenu;
import view.library.ILibraryView;
import view.library.IResourceListView;
import view.library.IResourceView;
import view.library.ITaskListView;
import view.library.ITaskView;
import view.oauth.IOauthHandlerView;

public interface IWorkflowGUIFactory{
	
	public void displayError(String title, String header, String message, boolean modal);
	
	public void displayMessage(String title, String header, String message, boolean modal);
	
	public boolean requestConfirmation(String title, String header, String message);
	
	public boolean requestTextInput(String title, String header, String message, String value);
	
	public void showConfigureDialog();
	
	public void showNewAccountDialog();
	
	public void showAccountListDialog();
	
	public void showNewLibraryDialog();
	
	public void showImportLibraryDialog();
	
	public void showNewTaskListDialog();
	
	public boolean showUnsavedResourcesDialog();
	
	public boolean showCachedFilesDialog();
	
	public List<File> showNativeFileChooser(Chooser.Mode mode, String title, File file, String... extensions);
	
	public List<File> showNativeFileChooser(Chooser.Mode mode, String title, String initialDirectory, String initialFilename, String... extensions);
	
// Factory Functions
	
	public ILibraryView getLibraryView(Library library);
	
	public ILibraryMenu getLibraryMenu(Library library);
	
	public ITaskListView getTaskListView(TaskList taskList);
	
	public ITaskView getTaskView(Task task);
	
	public IResourceView getResourceView(Resource resource);
	
	public IResourceListView getResourceListView(ResourceList resourceList);
	
	public IFileResourceView getFileResourceView(FileResource resource);
	
	public ILocalChooserPane getLocalChooserPane(String name, Chooser.Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions);
	
	public ILocalChooserDialog getLocalChooserDialog(String name, Chooser.Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions);
	
	public IRemoteChooserPane getRemoteChooserPane(Account account, String name, Chooser.Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions);
	
	public IRemoteChooserDialog getRemoteChooserDialog(Account account, String name, Chooser.Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions);
	
	public IOauthHandlerView getOauthDialog();
}
