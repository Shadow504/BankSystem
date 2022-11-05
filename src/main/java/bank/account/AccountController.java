package bank.account;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import bank.account.console.AccountConsole;
import bank.client.ClientClass;

//* Handles the accounts for a single client */
public class AccountController {
    //ACcount lists
    //The key is the BankAccount's id
    public Map<Integer, BankAccount> accountList = new HashMap<>();

    public BankAccount currentAccount = null;
    public ClientClass currentClient;

    AccountConsole currConsole;

    public AccountController(ClientClass currentClient) {
        this.currentClient = currentClient;

        currConsole = new AccountConsole(currentClient);
    }

    public void populateAccountList(ClientClass client) {
        try {
            
            ResultSet rs = AccountQueries.getAccountsForClient(client.id);

            while (rs.next()) {

                String name = rs.getString("name");
                String password = rs.getString("password");
            
                int balance = rs.getInt("balance");
                int accountId = rs.getInt("id");

                BankAccount account = new BankAccount(accountId, client.id, name, password, balance);

                accountList.put(accountId, account);
    
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createAccount(int client_id) {
        try { 

            int account_id = currConsole.beginSigninSession();
        
            CachedRowSet accountRow = AccountQueries.getNameAndPassWordFromId(account_id);

            String name = accountRow.getString("name");
            String password  = accountRow.getString("password");

            BankAccount account = new BankAccount(account_id, client_id, name, password, 0);

            accountList.put(AccountQueries.getLastInsertedId(name, password), account);

            currentAccount = account;

            AccountQueries.updateRecentLoggedInAccount(client_id, account_id);

            currConsole.beginAccountActions();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logIntoNewAccount() {
        try { 

            int accountId = currConsole.beginLoginSession();

            currentAccount = accountList.get(accountId);

            AccountQueries.updateRecentLoggedInAccount(currentClient.id, accountId);

            currConsole.beginAccountActions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void logIntoRecentAccount() {
        try {

            int recent_account_id = AccountQueries.getRecentLoggedInAccount(currentClient.id);

            currentAccount = accountList.get(recent_account_id);

            currConsole.beginAccountActions();

            System.out.println(currentClient.accountController.currentAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void switchAccount(int newAccountId) {
        currentAccount = accountList.get(newAccountId);
        System.out.println("Switched to the new account!");
    }
}