package controller.oauth;

import java.util.Locale;

import view.oauth.IOauthHandlerView;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

import controller.Core;
import model.account.Account;

public class DropboxOauthHandler extends OauthHandler{

	public boolean doAuth(Account account){
		
		if(account.getType() == Account.AccountType.ACCOUNT_TYPE_DROPBOX){
		
			//give the user a chance to bail on this operation first
			//if (!Core.getGuiFactory().requestConfirmation("Authorization Required", null, "Workflow requires access to your Dropbox account. Click OK to be directed to the Dropbox website to grant this access.")) {
			//	return false;
			//}else{
			
				//do whatever dropbox requires for oauth until we get a URL to direct the user to
				DbxAppInfo appInfo = new DbxAppInfo(DROPBOX_APP_ID, DROPBOX_APP_SECRET);
		        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
		        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
				
				//execute the common segment of the code; this directs the user to login and start an oauth request session
		        IOauthHandlerView view = Core.getGuiFactory().getOauthDialog();

				//retrieve the auth token (if present) and attempt to get an access token with it
				if(view.requestOauthAuthToken(webAuth.start()) && view.getToken() != null){
					try {
						DbxAuthFinish authFinish = webAuth.finish(view.getToken());
						String accessToken = authFinish.accessToken;
						account.setToken(accessToken);
						return true;
					} catch (DbxException e) {
						e.printStackTrace();
						return false;
					}
				}
			//}
		}
		
		return false;
		
	}
	
}
