package view.chooser;

import java.io.File;
import java.util.ArrayList;

public interface ILocalChooserPane extends Chooser{
	
	public String getString();
	
	public ArrayList<File> getSelection();
	
}
