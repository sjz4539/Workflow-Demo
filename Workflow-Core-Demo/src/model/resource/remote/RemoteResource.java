package model.resource.remote;

import java.io.File;
import java.io.InputStream;

import model.account.Account;

public abstract class RemoteResource {

	protected String name, id;
	protected RemoteFolder parent = null;
	protected Account source;
	
	protected RemoteResource(String n, RemoteFolder p, Account s){
		name = n;
		parent = p;
		source = s;
		id = "";
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String i){
		id = i;
	}
	
	public Account getSource(){
		return source;
	}
	
	public RemoteFolder getParent(){
		return parent;
	}
	
	public void setParent(RemoteFolder p){
		parent = p;
	}
	
	public String getPath(){
		String path = File.separator + name;
		RemoteFolder cur = parent;
		while(cur != null){
			if(cur.getName() != null && cur.getName().length() > 0){
				path = File.separator + cur.getName() + path;
			}
			cur = cur.getParent();
		}
		return path;
	}
	
	public abstract InputStream getIconResourceStream();
	
	public abstract boolean isDirectory();
	
}
