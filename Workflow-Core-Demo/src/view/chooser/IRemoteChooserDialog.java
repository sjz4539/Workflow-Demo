package view.chooser;

import java.util.List;

import controller.SimpleHandler;
import model.resource.remote.RemoteResource;

public interface IRemoteChooserDialog extends Chooser{
	
	public void showDialog(SimpleHandler onAccept, SimpleHandler onCancel);
	
	public boolean showDialogAndWait();
	
	public String getString();
	
	public List<RemoteResource> getSelection();
	
}
