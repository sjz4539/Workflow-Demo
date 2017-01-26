package view.account;

import model.account.Account;

public interface IAccountListView{
	
	public enum Mode{
		MODE_VIEW, MODE_SELECT
	}
	
	public void updateUI();
	
	public Account getSelection();
	
}
