package model.account;

import java.io.Serializable;

import controller.io.DropboxFileOps;
import controller.io.GoogleFileOps;
import controller.io.FileOps;
import controller.io.LocalFileOps;
import controller.oauth.DropboxOauthHandler;
import controller.oauth.GoogleOauthHandler;
import controller.oauth.OauthHandler;

public class Account implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name, accessToken, refreshToken;
	private AccountType type;
	
	private transient FileOps fileOps = null;
	private transient OauthHandler oauth = null;
	
	public enum AccountType{
		ACCOUNT_TYPE_DROPBOX, ACCOUNT_TYPE_GOOGLE, ACCOUNT_TYPE_LOCAL
	}
	
	public Account(AccountType t){
		name = "";
		type = t;
	}
	
	public Account(String n, AccountType t){
		name = n;
		type = t;
	}
	
	public Account(String n, String at, String rt, AccountType t){
		name = n;
		accessToken = at;
		refreshToken = rt;
		type = t;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public AccountType getType(){
		return type;
	}
	
	public void setType(AccountType t){
		type = t;
	}
	
	public String getToken(){
		return accessToken;
	}
	
	public void setToken(String t){
		accessToken = t;
	}
	
	public String getRefreshToken(){
		return refreshToken;
	}
	
	public void setRefreshToken(String r){
		refreshToken = r;
	}
	
	public FileOps getFileOps(){
		if(fileOps == null){
			switch(type){
				case ACCOUNT_TYPE_DROPBOX:
					fileOps = new DropboxFileOps();
					return fileOps;
				case ACCOUNT_TYPE_GOOGLE:
					fileOps = new GoogleFileOps();
					return fileOps;
				case ACCOUNT_TYPE_LOCAL:
					fileOps = new LocalFileOps();
					return fileOps;
				default:
					return null;
			}
		}else{
			return fileOps;
		}
	}
	
	public OauthHandler getOauthHandler(){
		if(oauth == null){
			switch(type){
				case ACCOUNT_TYPE_DROPBOX:
					oauth = new DropboxOauthHandler();
					return oauth;
				case ACCOUNT_TYPE_GOOGLE:
					oauth = new GoogleOauthHandler();
					return oauth;
				default:
					return null;
			}
		}else{
			return oauth;
		}
	}
	
	public String getAuth(){
		if(accessToken == null){
			getOauthHandler().doAuth(this);
		}
		return accessToken;
	}
	
	public void clearAuth(){
		accessToken = null;
	}
	
	public void clearRefresh(){
		refreshToken = null;
	}
	
}
