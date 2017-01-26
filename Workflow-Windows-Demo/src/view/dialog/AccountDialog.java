package view.dialog;

import model.account.Account;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AccountDialog extends Dialog<ButtonType>{
	
	private final String accOptDropbox = "Dropbox";
	private final String accOptGoogle = "Google Drive";
	
	private Account account;
	
	private ComboBox<String> accType;
	private TextField name, token, refresh;
	
	public AccountDialog(){
		this(null);
	}
	
	public AccountDialog(Account a){
		account = a;
		generateUI();
		updateUI();
	}
	
	private void generateUI(){
		
		setTitle("Create/Edit Account");
		
		accType = new ComboBox<String>();
		accType.getItems().addAll(accOptDropbox, accOptGoogle);
		accType.setEditable(account != null);
		accType.setDisable(account != null);
		
		name = new TextField();
		name.setPromptText("Account Name");
		
		token = new TextField();
		token.setPromptText(account != null ? "******" : "OAuth Access Token (Optional)");
		
		refresh = new TextField();
		refresh.setPromptText(account != null ? "******" : "OAuth Refresh Token (Optional)");
		
		VBox formPane = new VBox(2);
		
		HBox typeRow = new HBox(2, new Label("Type:"), accType);
		HBox nameRow = new HBox(2, new Label("Name:"), name);
		HBox tokenRow = new HBox(2, new Label("Access Token:"), token);
		HBox refreshRow = new HBox(2, new Label("Refresh Token:"), refresh);
		
		formPane.getChildren().addAll(typeRow, nameRow, tokenRow, refreshRow);
		
		getDialogPane().setContent(formPane);
		getDialogPane().getButtonTypes().clear();
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
	}
	
	private void updateUI(){
		if(account != null){
			switch(account.getType()){
				case ACCOUNT_TYPE_DROPBOX:
					accType.getSelectionModel().select(accOptDropbox);
					break;
				case ACCOUNT_TYPE_GOOGLE:
					accType.getSelectionModel().select(accOptGoogle);
					break;
				default:
					break;
			}
			name.setText(account.getName());
		}else{
			accType.getSelectionModel().select(accOptDropbox);
		}
	}
	
	public Account makeAccount(){
		if(account == null){
			account = new Account(name.getText(), accType.getSelectionModel().getSelectedItem().equals(accOptDropbox) ? Account.AccountType.ACCOUNT_TYPE_DROPBOX : Account.AccountType.ACCOUNT_TYPE_GOOGLE);
		}else{
			account.setName(name.getText());
			account.setType(accType.getSelectionModel().getSelectedItem().equals(accOptDropbox) ? Account.AccountType.ACCOUNT_TYPE_DROPBOX : Account.AccountType.ACCOUNT_TYPE_GOOGLE);
		}
		
		if(token.getText().length() > 0){
			account.setToken(token.getText());
		}
		if(refresh.getText().length() > 0){
			account.setRefreshToken(refresh.getText());
		}
		
		return account;
	}
	
}
