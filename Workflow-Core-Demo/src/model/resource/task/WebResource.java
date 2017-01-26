package model.resource.task;

import view.library.IResourceView;
import controller.library.Library;
import controller.library.Task;

public class WebResource extends Resource{

	private static final long serialVersionUID = 1L;
	
	private String link;
	private String name;
	
	public WebResource(Library l){
		super(l);
	}
	
	public WebResource(WebResource wr){
		super(wr);
		link = wr.getLink();
	}
	
	public WebResource(Task t, String l){
		this(t, l, l);
	}
	
	public WebResource(Task t, String l, String n){
		super(t);
		link = l;
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public void setView(IResourceView v){
		view = v;
	}
	
	public String getLink(){
		return link;
	}
	
	public void setLink(String l){
		link = l;
		if(view != null){
			view.updateUI();
		}
	}
	
	public String getCommandString(){
		return link;
	}

	public WebResource copyObject() {
		return new WebResource(this);
	}

	public boolean storedAsFile() {
		return false;
	}
	
}
