package controller.oauth;

import java.io.IOException;
import java.util.Collections;

import view.oauth.IOauthHandlerView;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import controller.Core;
import model.account.Account;

public class GoogleOauthHandler extends OauthHandler{

	public boolean doAuth(Account account){
		
		if(account.getType() == Account.AccountType.ACCOUNT_TYPE_GOOGLE){
			
			//if a refresh token exists, try getting a new access token that way first.
			if(account.getRefreshToken() != null){
				
				try {
					
					GoogleTokenResponse authFinish = new GoogleAuthorizationCodeTokenRequest(
							new NetHttpTransport(), new JacksonFactory(), 
							GOOGLE_APP_ID, GOOGLE_APP_SECRET, 
							account.getRefreshToken(), null)
					.setGrantType("refresh_token")
					.setScopes(Collections.singleton("https://www.googleapis.com/auth/drive"))
					.execute();
				
					account.setToken(authFinish.getAccessToken());

					return true;
					
				} catch (TokenResponseException tre) {
					tre.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//something went wrong, blank the refresh token and try again to get a new refresh and access token set.
				account.setRefreshToken(null);
				return doAuth(account);
				
			}else{
			
				//we need to get a new auth token so we can get a new access/refresh token
			
				//give the user a chance to bail on this operation first
				//if (!Core.getGuiFactory().requestConfirmation("Authorization Required", null, "Workflow requires access to your Google Drive account. Click OK to be directed to a Google website to grant this access.")) {
				//	return false;
				//}else{
				
				GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
						new NetHttpTransport(), new JacksonFactory(), 
						GOOGLE_APP_ID, GOOGLE_APP_SECRET, 
						Collections.singleton("https://www.googleapis.com/auth/drive")
				).setAccessType("offline").build();
				
				//execute the common segment of the code; this directs the user to login and start an oauth request session
				IOauthHandlerView view = Core.getGuiFactory().getOauthDialog();
				
				//retrieve the auth token (if present) and attempt to get an access token with it
				if(view.requestOauthAuthToken(flow.newAuthorizationUrl().setRedirectUri("urn:ietf:wg:oauth:2.0:oob").build()) && view.getToken() != null){

					try {
						
						GoogleTokenResponse authFinish = new GoogleAuthorizationCodeTokenRequest(
								new NetHttpTransport(), new JacksonFactory(), 
								GOOGLE_APP_ID, GOOGLE_APP_SECRET, 
								view.getToken(), null)
						.setGrantType("authorization_code")
						.setScopes(Collections.singleton("https://www.googleapis.com/auth/drive"))
						.execute();
					
						account.setToken(authFinish.getAccessToken());
						account.setRefreshToken(authFinish.getRefreshToken());

						return true;
						
					} catch (TokenResponseException tre) {
						tre.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//something went wrong, blank the access token and refresh token.
					account.setToken(null);
					account.setRefreshToken(null);
					
				}
				//}
			}
		}
		
		//return false on any kind of fatal error
		return false;
		
	}
	
}
