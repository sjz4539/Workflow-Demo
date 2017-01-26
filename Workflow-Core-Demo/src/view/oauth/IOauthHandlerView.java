package view.oauth;

public interface IOauthHandlerView {

	public boolean requestOauthAuthToken(String authURI);
	
	public String getToken();
	
}
