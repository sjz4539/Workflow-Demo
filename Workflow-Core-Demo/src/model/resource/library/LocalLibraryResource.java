package model.resource.library;

import controller.library.LocalLibrary;

public class LocalLibraryResource extends LibraryResource{

	private static final long serialVersionUID = 1L;

	public LocalLibraryResource(String p, LocalLibrary l){
		super(p, l);
	}
	
	public LocalLibraryResource copyObject() {
		return null;
	}

	public boolean storedRemotely() {
		return false;
	}

}
