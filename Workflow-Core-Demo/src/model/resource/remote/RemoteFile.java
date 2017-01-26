package model.resource.remote;

import java.io.InputStream;

import model.account.Account;

public class RemoteFile extends RemoteResource{
	
	public RemoteFile(String n, RemoteFolder p, Account s){
		super(n, p, s);
	}

	public boolean isDirectory() {
		return false;
	}

	public InputStream getIconResourceStream() {
		return RemoteFile.class.getResourceAsStream("../../../resources/document_16.png");
	}
	
}
