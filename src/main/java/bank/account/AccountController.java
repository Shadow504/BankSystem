package bank.account;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import bank.account.console.AccountConsole;
import bank.client.ClientClass;

//* Handles the accounts for a single client */
public class AccountController {
    //ACcount lists
    //The key is the BankAccount's id
    public Map<Integer, BankAccount> accountList = new HashMap<>();

    public BankAccount currentAccount = null;
    public ClientClass currentClient;

    public AccountConsole currConsole;

    AccountDAO accountDAO;

    public AccountController(ClientClass currentClient, AccountDAO accountDAO) {
        this.currentClient = currentClient;
        this.accountDAO = accountDAO;

        currConsole = new AccountConsole(currentClient, accountDAO);
    }

    public void populateAccountList() {
        try {
            
            ResultSet rs = accountDAO.getAccountsForClient(currentClient.id);

            while (rs.next()) {

                String name = rs.getString("name");
                String password = rs.getString("password");
            
                int balance = rs.getInt("balance");
                int accountId = rs.getInt("id");

                BankAccount account = new BankAccount(accountDAO, accountId, currentClient.id, name, password, balance);

                accountList.put(accountId, account);
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BankAccount createAccount() {
        try { 
            // Retrieve the inputed names and password
            String[] userAndPass = currConsole.beginSigninSession();
            // Insert the account into the database, and get its id

            String name = userAndPass[0];
            String password = userAndPass[1];

            System.out.println(currentClient.id);
            int account_id  = accountDAO.insertAccount(currentClient.id, userAndPass[0], userAndPass[1]);

            BankAccount account = new BankAccount(accountDAO, account_id, currentClient.id, name, password, 0);

            accountList.put(account_id, account);

            currentAccount = account;

            accountDAO.updateRecentLoggedInAccount(currentClient.id, account_id);

            currConsole.beginAccountActions();

            return account;
 
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void logIntoNewAccount() {
        try { 

            int accountId = currConsole.beginLoginSession();

            currentAccount = accountList.get(accountId);

            accountDAO.updateRecentLoggedInAccount(currentClient.id, accountId);

            currConsole.beginAccountActions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logIntoRecentAccount() {
        try {

            int recent_account_id = accountDAO.getRecentLoggedInAccount(currentClient.id);

            currentAccount = accountList.get(recent_account_id);

            currConsole.beginAccountActions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void switchAccount(int newAccountId) {
        currentAccount = accountList.get(newAccountId);
        System.out.println("Switched to the new account!");
    }
}