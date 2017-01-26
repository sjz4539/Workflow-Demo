package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import view.IWorkflowGUI;
import view.IWorkflowGUIFactory;
import model.account.Account;
import model.resource.cache.ResourceCache;
import controller.io.ResourceActionService;
import controller.library.Library;
import controller.library.RemovableLibrary;

/**
 * Core logic class. Handles initialization and
 * ties the whole program together.
 * 
 * @author Test
 *
 */
public class Core{
	
	public static final String CONFIG_FILE_NAME = "config.properties";
	public static final String LIBRARY_FILE_NAME = "config.libraries";
	
	//config properties
	public static final String PROP_CACHE_ROOT = "cache_root";
	public static final String DEF_CACHE_ROOT = "";
	public static final String PROP_MAX_SIMULTANEOUS_DOWNLOADS = "max_simul_downloads";
	public static final String DEF_MAX_SIMULTANEOUS_DOWNLOADS = "10";
	public static final String PROP_MAX_SIMULTANEOUS_UPLOADS = "max_simul_uploads";
	public static final String DEF_MAX_SIMULTANEOUS_UPLOADS = "10";
	
	private static Core core = null;
	private static ICoreExtension extension = null;
	private static IWorkflowGUI gui = null;
	private static IWorkflowGUIFactory guiFactory = null;
	private static IProcessMonitor procMon = null;
	private static ResourceCache resCache = null;
	private Properties properties;
	
	private Library curLibrary;
	private ArrayList<Library> libraries;
	private ArrayList<RemovableLibrary> removableLibraries = new ArrayList<RemovableLibrary>();
	private ArrayList<Account> accounts;
	
//============================================================================
	
	//--Library Functions--//

	public static Core getCore(){
		if(core == null){
			core = new Core();
		}
		return core;
	}
	
	public static void setGui(IWorkflowGUI g){
		gui = g;
	}
	
	public static IWorkflowGUI getGui(){
		return gui;
	}
	
	public static void setGuiFactory(IWorkflowGUIFactory factory){
		guiFactory = factory;
	}
	
	public static IWorkflowGUIFactory getGuiFactory(){
		return guiFactory;
	}
	
	public static void setProcessMonitor(IProcessMonitor pm){
		procMon = pm;
	}
	
	public static IProcessMonitor getProcessMonitor(){
		return procMon;
	}
	
	public static void setCoreExtension(ICoreExtension ex){
		extension = ex;
	}
	
	public static ICoreExtension getCoreExtension(){
		return extension;
	}
	
	public static ResourceCache getResourceCache(){
		if(resCache == null){
			if(getCore().getProperty(PROP_CACHE_ROOT) == null || getCore().getProperty(PROP_CACHE_ROOT).isEmpty()){
				getCore().setProperty(PROP_CACHE_ROOT, ResourceCache.genCacheFolder());
				getCore().saveSettings();
				return Core.getResourceCache();
			}else{
				resCache = new ResourceCache(getCore().getProperty(PROP_CACHE_ROOT));
			}
		}
		return resCache;
	}
	
//============================================================================

	//--Public Instance Functions--//
	
	//perform initialization tasks:
	//	-read in settings
	//	-create data structures
	public boolean init(){
		//read in settings, set up listeners/handlers/etc
		//if any errors occur, return false.
		return readSettings() && loadLibraryData() && createCache();
	}
	
	public boolean close(){
		return checkUnsavedResources() && saveLibraryData() && saveSettings() && destroyCache();
	}
	
	public String getProperty(String propKey){
		return properties.getProperty(propKey);
	}
	
	public void setProperty(String propKey, String propVal){
		properties.setProperty(propKey, propVal);
	}
	
	public void addAccount(Account a){
		if(!accounts.contains(a)){
			accounts.add(a);
		}
	}
	
	public Account getAccount(int i){
		if(accounts.size() > i){
			return accounts.get(i);
		}else{
			return null;
		}
	}
	
	public ArrayList<Account> getAccounts(){
		return accounts;
	}
	
	public Account removeAccount(int i){
		if(accounts.size() > i){
			return accounts.remove(i);
		}else{
			return null;
		}
	}
	
	public void removeAccount(Account a){
		accounts.remove(a);
	}
	
	public void addLibrary(Library library, boolean setCurrent){
		if(library.isRemovable()){
			removableLibraries.add((RemovableLibrary)library);
		}else{
			libraries.add(library);
		}
		getGui().updateLibraryList();
		if(setCurrent){
			setCurrentLibrary(library);
		}
	}
	
	public Library getLibrary(int i){
		return libraries.get(i);
	}
	
	public List<Library> getLibraries(){
		ArrayList<Library> ret = new ArrayList<Library>();
		ret.addAll(libraries);
		ret.addAll(removableLibraries);
		return ret;
	}
	
	public void removeLibrary(Library l){
		libraries.remove(l);
		if(getCurrentLibrary().equals(l)){
			setCurrentLibrary(null);
		}
		getGui().updateLibraryList();
	}
	
	public void setCurrentLibrary(Library l){
		if(l != null){
			if(l.isLoaded() || l.isNew() || l.loadTasks()){
				Library oldLib = curLibrary;
				curLibrary = l;
				curLibrary.getMenu().updateUI();
				if(oldLib != null){
					oldLib.getMenu().updateUI();
				}
				getGui().setLibraryView(curLibrary.getView());
			}else if(l.getType() == Library.Type.REMOTE){
				//attempt to load the library first, then set it as active after.
				final Library lib = l;
				ResourceActionService.loadResource(
						l.getResource(), 
						new SimpleHandler(){
							public void handle() {
								setCurrentLibrary(lib);
							}
						},
						null
				);
			}
		}else{
			getGui().setLibraryView(null);
		}
	}
	
	public Library getCurrentLibrary(){
		return curLibrary; 
	}
	
//============================================================================
	
	//--Private Instance Functions--//
	
	//basic constructor
	private Core(){
		
	}
	
	public boolean readSettings(){
		File config = new File(getCoreExtension().getStorageRoot() + File.separator + Core.CONFIG_FILE_NAME);
		
		if(properties == null){
			properties = new Properties();
		}else{
			properties.clear();
		}
		
		if(config.exists()){
			try {
				FileReader reader = new FileReader(config);
				properties.load(reader);
				reader.close();
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			return Core.getCore().generateConfigFile();
		}
	}
	
	public boolean saveSettings(){
		File config = new File(getCoreExtension().getStorageRoot() + File.separator + Core.CONFIG_FILE_NAME);
		
		try {
			FileWriter writer = new FileWriter(config);
			properties.store(writer, "");
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//Sets properties to defaults. If no Properties
	//object exists, one is created.
	private void setDefaultSettings(){
		if(properties == null){
			properties = new Properties();
		}
		
		//set defaults, define default values alongside key strings
		properties.setProperty(PROP_MAX_SIMULTANEOUS_DOWNLOADS, DEF_MAX_SIMULTANEOUS_DOWNLOADS);
		properties.setProperty(PROP_MAX_SIMULTANEOUS_UPLOADS, DEF_MAX_SIMULTANEOUS_UPLOADS);
		
	}
	
	//creates a properties object with default values,
	//then writes it to disk.
	public boolean generateConfigFile(){
		setDefaultSettings();
		return saveSettings();
	}
	
	//Load tasks from the location loaded with properties
	private boolean loadLibraryData(){
		if(libraries == null){
			libraries = new ArrayList<Library>();
		}
		
		File libraryFile = new File(LIBRARY_FILE_NAME + Library.LIBRARY_FILE_EXTENSION);
		
		if(libraryFile.exists()){
			try {
				FileInputStream fileIn = new FileInputStream(libraryFile);
				ObjectInputStream objIn = new ObjectInputStream(fileIn);
				
				AbstractMap.SimpleEntry<ArrayList<Library>, ArrayList<Account>> data = (AbstractMap.SimpleEntry<ArrayList<Library>, ArrayList<Account>>)objIn.readObject();
				
				libraries = data.getKey();
				accounts = data.getValue();
				
				objIn.close();
				fileIn.close();
				
				return true;
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			libraries = new ArrayList<Library>();
			accounts = new ArrayList<Account>();
			return true;
		}
	}
	
	public RemovableLibrary loadRemovableLibraryData(String path){
		if(removableLibraries == null){
			removableLibraries = new ArrayList<RemovableLibrary>();
		}
		
		File libraryFile = new File(path);
		
		if(libraryFile.exists()){
			try {
				FileInputStream fileIn = new FileInputStream(libraryFile);
				ObjectInputStream objIn = new ObjectInputStream(fileIn);
				
				RemovableLibrary importedLib = (RemovableLibrary)objIn.readObject();
				
				//adjust the library's root based on the provided path
				String oldRoot = importedLib.getRoot();
				oldRoot = (oldRoot.endsWith(File.separator) ? oldRoot.substring(0, oldRoot.length() - 1) : oldRoot);
				int pos = oldRoot.lastIndexOf(File.separator);
				String rootName = oldRoot.substring(pos + 1);
				
				//find the root folder's name in the given path.
				//set the library's root folder to everything in the given path up to and including that match.
				pos = path.lastIndexOf(rootName);
				if(pos != -1){
					importedLib.setRoot(path.substring(0, pos + rootName.length()));
					objIn.close();
					fileIn.close();
					return importedLib;
				}else{
					objIn.close();
					fileIn.close();
					return null;
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
	private boolean saveLibraryData(){
		if(libraries == null){
			libraries = new ArrayList<Library>();
		}
		if(accounts == null){
			accounts = new ArrayList<Account>();
		}
		
		File libraryFile = new File(LIBRARY_FILE_NAME + Library.LIBRARY_FILE_EXTENSION);
		
		try {
			FileOutputStream fileOut = new FileOutputStream(libraryFile);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			
			objOut.writeObject(new AbstractMap.SimpleEntry<ArrayList<Library>, ArrayList<Account>>(libraries, accounts));
			
			objOut.close();
			fileOut.close();
			
			if(removableLibraries != null){
				for(RemovableLibrary rl : removableLibraries){
					if(!saveRemovableLibraryData(rl)){
						return false;
					}
				}
			}
			
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	private boolean saveRemovableLibraryData(RemovableLibrary rl){
		
		try{
			
			File removableFile = new File(rl.getRoot() + File.separator + rl.getName() + Library.REMOVABLE_LIBRARY_FILE_EXTENSION);
			FileOutputStream remFileOut = new FileOutputStream(removableFile);
			ObjectOutputStream remObjOut = new ObjectOutputStream(remFileOut);
			
			remObjOut.writeObject(rl);
			
			remObjOut.close();
			remFileOut.close();
			
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//Scan all libraries for any resources with changes that need uploading.
	private boolean checkUnsavedResources(){
		while(!getResourceCache().checkUnsavedResources()){
			if(!getGuiFactory().showUnsavedResourcesDialog()){
				return false;
			}
		}
		return true;
	}
	
	private boolean createCache(){
		return getResourceCache().init();
	}
	
	private boolean destroyCache(){
		while(!getResourceCache().deleteAll()){
			if(!getGuiFactory().showCachedFilesDialog()){
				return false;
			}
		}
		return true;
	}
	
}
