package view.form;

import javafx.scene.layout.VBox;
import controller.Core;
import view.chooser.LocalChooserPane;
import view.chooser.Chooser;

public class PropertiesForm extends VBox implements IPropertiesForm{

	private LocalChooserPane cacheRootPane;
	
	public PropertiesForm(){
		
		String cacheRoot = Core.getCore().getProperty(Core.PROP_CACHE_ROOT);
		
		//create form objects
		cacheRootPane = new LocalChooserPane("File Cache Location:", Chooser.Mode.MODE_SINGLE_FOLDER, false, cacheRoot, null);
		
		//assemble GUI
		getChildren().add(cacheRootPane);
	}
	
	public void saveProperties(){
		Core.getCore().setProperty(Core.PROP_CACHE_ROOT, cacheRootPane.getString());
	}
	
}
