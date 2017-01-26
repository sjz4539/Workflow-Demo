package controller.library;

public class RemovableLibrary extends LocalLibrary{

	private static final long serialVersionUID = 1L;

	public RemovableLibrary(String n, String r, String p) {
		super(n, r, p);
	}

	public boolean isRemovable(){
		return true;
	}
	
	public void setRoot(String r){
		root = r;
	}
}
