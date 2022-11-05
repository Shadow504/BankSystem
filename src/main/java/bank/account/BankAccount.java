package bank.account;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import bank.Bank;
import bank.BankDataConnection;
import bank.Transactions;

public class BankAccount {


    static MysqlConnectionPoolDataSource ds = BankDataConnection.ds;

    static String updateBalance = "UPDATE accounts SET balance = ? WHERE id = ?";

    private void updateBalanceInDatabase() {
        AccountQueries.updateAnAccountBalance(this.id, this.balance);
    }

    public int balance;
    public int clientId;

    public int id;
    public String accountName; 

    private String accountPassword;

    public BankAccount(int id, int client_id, String accountName, String accountPassword, int balance) {
        this.accountName = accountName;
        this.accountPassword = accountPassword;

        this.clientId = client_id;
        this.id = id;
        this.balance = balance;
    }

    //* Should be thread-safe, thwart running conditions */
    public synchronized void incrementbalance(int amount) {

        this.balance += amount;

        updateBalanceInDatabase();
    }

    protected boolean authorizeWithdrawal(int amount) {
        if (this.balance < amount) {
            System.out.println("Insufficient balance to withdraw");

            return false;
        }    

        if (amount < Bank.minWithdrawAmount) {
            System.out.println("Does not meet the minimum requirement to withdraw");

            return false;
        }

        return true;
    }

    protected boolean authorizeDeposit(int amount) {
        if (amount < Bank.minDepositAmount) {
            System.out.println("Does not meet the minimum requirement to deposit");

            return false;
        }

        return true;
    }

    public boolean withdraw(int amount) {  
        if (!authorizeWithdrawal(amount)) {
            return false;
        }                                 

        Transactions.logStandardTransaction(this, -amount);

        incrementbalance(-amount);

        return true;
    }

    public boolean deposit(int amount) {
        if (!authorizeDeposit(amount)) {return false;}

        Transactions.logStandardTransaction(this, amount);

        incrementbalance(amount);

        return true;
    }

    public void transfer(int toAccountID, int amount) {

        this.incrementbalance(-amount);

        AccountQueries.updateAnAccountBalance(toAccountID, amount);

        Transactions.logTransfer(this.id, toAccountID, amount);
    }

    public void authorizeCash(int amount) {
        if (amount <= 0) { System.out.println("Inbalid amount"); }
    }

    public boolean authorizeTransaction(int amount) {
        if (this.balance < amount) {
            System.out.println("Insufficient balance to transfer");
            return false;
        }

        if (amount < Bank.minTransferAmount) {
           
            System.out.println("Does not meet the minimum requirement to transfer");
        }

        return true;
    }
}