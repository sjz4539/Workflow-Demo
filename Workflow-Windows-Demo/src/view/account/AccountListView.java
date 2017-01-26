package view.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import view.dialog.AccountDialog;
import controller.Core;
import model.account.Account;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class AccountListView extends Dialog<ButtonType> implements IAccountListView{
	
	private static final int MODE_VIEW = 0;
	private static final int MODE_SELECT = 1;
	
	public static final ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
	public static final ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
	
	private int mode;
	private Account.AccountType[] filter;
	
	protected ListView<Account> accountList;
	
	public AccountListView(){
		
		mode = MODE_VIEW;
		filter = new Account.AccountType[]{};
		
		generateUI();
		updateUI();
	}
	
	public AccountListView(int m, Account.AccountType... displayTypes){
		mode = m;
		filter = Arrays.copyOf(displayTypes, displayTypes.length);
	}
	
	private void generateUI(){
		
		Dialog<ButtonType> dialog = this;
		
		setTitle(mode == MODE_VIEW ? "Account Management" : "Select an account");
		
		BorderPane dialogPane = new BorderPane();
		
		accountList = new ListView<Account>();
		accountList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		accountList.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>(){
			public ListCell<Account> call(ListView<Account> arg0) {
				
				ListCell<Account> newCell = new ListCell<Account>(){
					public void updateItem(Account res, boolean empty){
						super.updateItem(res, empty);
						setGraphic(null);
						if(empty){
							setText("");
						}else{
							setText(res.getName());
						}
					}
				};
				
				newCell.setOnMouseClicked(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent e) {
						if(e.getButton().equals(MouseButton.PRIMARY)){
							if(e.getClickCount() >= 2){
								if(getSelection() != null){
									dialog.setResult(ButtonType.OK);
									dialog.close();
								}
							}
						}
					}
				});
				
				return newCell;
			}
		});
		
		Button newAccountButton = new Button("New Account");
		newAccountButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				AccountDialog newAccountForm = new AccountDialog();
				
				Optional<ButtonType> response = newAccountForm.showAndWait();
				
				if(response.isPresent() && response.get().equals(ButtonType.OK)){
					Core.getCore().addAccount(newAccountForm.makeAccount());
					updateUI();
				}
			}
		});
		Button removeAccountButton = new Button("Remove Account");
		removeAccountButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				if(accountList.getSelectionModel().getSelectedItem() != null){
					Core.getCore().removeAccount(accountList.getSelectionModel().getSelectedItem());
					updateUI();
				}
			}
		});
		
		HBox buttonPane = new HBox();
		buttonPane.getChildren().addAll(newAccountButton, removeAccountButton);
		
		setDialogPane(new DialogPane(){
			
			protected Node createButton(ButtonType type){
				
		        final Button button = new Button(type.getText());
		        final ButtonData buttonData = type.getButtonData();
		        ButtonBar.setButtonData(button, buttonData);
		        button.setDefaultButton(type != null && buttonData.isDefaultButton());
		        button.setCancelButton(type != null && buttonData.isCancelButton());
		        button.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent event) {
						if (dialog != null) {
							dialog.setResult(type);
							dialog.close();
						}
					}
		        });
		        
		        if(mode == MODE_SELECT && type.equals(ok)){ //disable ok button when nothing is selected in select mode
		        	accountList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>(){
		    			public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
		    				button.setDisable(newValue != null && observable.getValue() != null);
		    			}
		    		});
		        }
		        
		        return button;
		        
			}
			
		});
		
		getDialogPane().getButtonTypes().add(ok);
		if(mode == MODE_SELECT){
			getDialogPane().getButtonTypes().add(cancel);
		}
		
		ScrollPane scrollPane = new ScrollPane(accountList);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		dialogPane.setCenter(scrollPane);
		dialogPane.setTop(buttonPane);
		
		getDialogPane().setContent(dialogPane);
		
	}
	
	public void updateUI(){
		
		ArrayList<Account> accounts = new ArrayList<Account>();
		
		if(filter.length == 0){
			accounts = Core.getCore().getAccounts();
		}else{
			for(Account.AccountType type : filter){
				for(Account account : Core.getCore().getAccounts()){
					if(account.getType() == type){
						accounts.add(account);
					}
				}
			}
		}
		
		accountList.getItems().clear();
		accountList.getItems().addAll(accounts);
		
	}
	
	public Account getSelection(){
		return accountList.getSelectionModel().getSelectedItem();
	}
	
}
