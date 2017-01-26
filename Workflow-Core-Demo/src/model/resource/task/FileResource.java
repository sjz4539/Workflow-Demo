package model.resource.task;

import java.io.File;

import view.library.IFileResourceView;
import controller.Core;
import controller.SimpleHandler;
import controller.library.Library;
import controller.library.Task;

public class FileResource extends Resource{
	
	private static final long serialVersionUID = 1L;
	
	protected transient File file; //a file object representing where this file is stored.
	protected transient long saved, updated; //timestamps for use by the file cache system.
	protected transient Status status;
	private transient IFileResourceView view;
	
	protected String path; //the path this file is located at, relative to a root folder.
	
	public enum Status{
		NORMAL, ERROR, DOWNLOADING, UPLOADING, COPYING, MOVING, DELETING, MISSING
	}
	
	public static final String STATUS_NORMAL = "";
	public static final String STATUS_ERROR = "";
	public static final String STATUS_DOWNLOADING = "Downloading...";
	public static final String STATUS_UPLOADING = "Uploading...";
	public static final String STATUS_COPYING = "Copying...";
	public static final String STATUS_MOVING = "Moving...";
	public static final String STATUS_DELETING = "Deleting...";
	public static final String STATUS_MISSING = "Missing!";
	
	public FileResource(Library l){
		super(l);
	}
	
	public FileResource(FileResource f) {
		super(f);
		path = f.getPath();
	}

	public FileResource(Task t, String p) {
		super(t);
		setPath(p);
	}
	
	public void setView(IFileResourceView v){
		view = v;
	}
	
	public IFileResourceView getView(){
		if(view == null){
			view = Core.getGuiFactory().getFileResourceView(this);
		}
		return view;
	}
	
	public String getName(){
		int a = path.lastIndexOf(File.separator);
		
		if(a > -1){
			return path.substring(a + 1, path.length());
		}else{
			return path;
		}
	}
	
	public void setName(String n){
	}

	public String getPath(){
		return path;
	}
	
	public String getAbsolutePath(){
		return library.getRoot() + File.separator + path;
	}
	
	public String getFolder(){
		String absPath = getAbsolutePath();
		int a = absPath.lastIndexOf(File.separator);
		
		if(a > 0){
			return absPath.substring(0, a);
		}else if (a == 0){
			return File.separator;
		}else{
			return absPath;
		}
	}
	
	public void setPath(String p){
		path = p;
		String root = library.getRoot();
		
		if(root != null && root.length() > 0){
			int posA = -1, posB = -1;
			posA = path.indexOf(root + File.separator);
			posB = path.indexOf(root);
			if(posA > -1){
				path = path.substring(posA + root.length() + File.separator.length());
			}else if(posB > -1){
				path = path.substring(posB + root.length());
			}
		}
		file = null;
	}
	
	public void setStatus(Status s){
		setStatus(s, "");
	}
	
	public void setStatus(Status s, String m){
		status = s;
		if(view != null){
			view.updateStatus(m);
		}
		getParentTask().childStatusChanged(s == Status.ERROR || s == Status.MISSING);
	}
	
	public Status getStatus(){
		if(status == null){
			status = Status.NORMAL;
		}
		return status;
	}
	
	public void setLastSavedTime(long ft){
		saved = ft;
	}
	
	public long getLastSavedTime(){
		return saved;
	}
	
	public void setUpdatedTime(long ft){
		updated = ft;
	}
	
	public long getUpdatedTime(){
		return updated;
	}
	
	public String getCommandString(){
		if(library.getType() == Library.Type.LOCAL){
			return getAbsolutePath();
		}else{
			return Core.getResourceCache().getAbsolutePath(this);
		}
	}
	
//==========================================================
	
	public boolean isLoaded(){
		if(library.getType() == Library.Type.LOCAL){
			return new File(getAbsolutePath()).exists();
		}else{
			return Core.getResourceCache().containsFileData(this);
		}
	}
	
	public boolean storedAsFile() {
		return true;
	}
	
	public boolean needsSave(){
		if(library.getType() == Library.Type.REMOTE && Core.getResourceCache().containsFileData(this)){
			long modified;
			File file = new File(Core.getResourceCache().getAbsolutePath(this));
			modified = (file.exists() ? file.lastModified() : -1);
			return getLastSavedTime() == -1 || getLastSavedTime() < modified;
		}else{
			return false;
		}
	}
	
	public FileResource copyObject() {
		return new FileResource(this);
	}
	
	public void save(){
		library.saveResource(this);
	}
	
	public void load(){
		library.loadResource(this);
	}
	
	public void open(){
		if(!isLoaded()){
			final FileResource me = this;
			library.loadResource(
				this, 
				new SimpleHandler(){
					public void handle() {
						me.open();
					}
				},
				null
			);
		}else{
			Core.getProcessMonitor().open(this);
		}
	}
	
}
