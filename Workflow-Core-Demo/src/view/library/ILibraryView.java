package view.library;

import model.resource.library.LibraryResource;

public interface ILibraryView{
	
	public void updateUI();
	
	public void setResource(LibraryResource lr);
	
	public void setTaskListView(ITaskListView tlv);

}
