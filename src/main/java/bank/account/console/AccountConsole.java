package bank.account.console;

import java.sql.SQLException;
import java.util.Scanner;

import bank.Bank;
import bank.account.AccountDAO;
import bank.account.BankAccount;
import bank.client.ClientClass;
import shared.Shared;

//* Handles all of the input regarding Accounts */

public class AccountConsole {

    private Scanner input = Shared.input;

    private static String escLine = "=====================================";

    private ConsoleActions consoleActions;
    private ClientClass client;

    private LoginClass loginClass;
    private SigninClass signinClass;

    //* Composition instead of inheritance (More flexible) */
    // Does not immediately print as the client can still be logging in
    public AccountConsole(ClientClass client, AccountDAO accountDAO) {

        consoleActions = new ConsoleActions(client, accountDAO);

        loginClass = new LoginClass(client, accountDAO);
        signinClass = new SigninClass(client, accountDAO);

        this.client = client;
    }

    public String[] beginSigninSession() throws SQLException {
        return signinClass.beginSigninSession();
    }

    public int beginLoginSession() throws SQLException {
        return loginClass.beginLoginSession();
    }

    public static void printAccountConsole(BankAccount currAccount) {
        System.out.println(
            escLine + "\n" +
            "Account Logged in: " + currAccount.accountName + "\n" +
            "Choose an Action:" + "\n" +
            "1: Check Balance" + "\n" +
            "2: Withdraw Cash" + "\n" +
            "3: Deposit Cash" + "\n" +
            "4: Apply for a Loan" + "\n" +
            "5: Transfer Money" + "\n" +
            "6: Exit Account" + "\n" +
            escLine);
    }

    public static void printTransferConsole() {
        System.out.println(
            escLine + "\n" +
            "Choose an Action:" + "\n" +
            "1: Transfer to another client's account" + "\n" +
            "2: Transfer to this client's account" + "\n" +
            "3: Exit" + "\n" +
            escLine);
    }

    public void beginAccountActions() {
        BankAccount currentAccount = client.getCurrentAccount();

        if (currentAccount == null) {
            System.out.println("Cannot begin an account actions session as there's no account logged in"); 
            return;
        }

        AccountConsole.printAccountConsole(currentAccount);

        String choice;

        try {
            boolean exitInput = false;
            
            while ((choice = input.nextLine()) != null) {

                switch (choice) {
                    case "1":
                        System.out.println("Current balance: " + currentAccount.balance);
                        break;
                    case "2":

                        consoleActions.beginWithdrawing();

                        break;
                    case "3":
                        consoleActions.beginDepositing();
    
                        break;

                    case "4":

                        System.out.println("Enter loan amount: ");
                        int amount = Integer.parseInt(input.nextLine());
                        
                        if (amount < Bank.minLoanAmount) { System.out.println("Invalid amount"); break; }

                        client.loanHandler.applyLoan(currentAccount, amount);

                        Thread.sleep(2000);
    
                        AccountConsole.printAccountConsole(currentAccount);

                        break;

                    case "5":

                        beginTransfering();

                        Thread.sleep(2000);
    
                        AccountConsole.printAccountConsole(currentAccount);

                        break;

                    case "6":
                        System.out.println("Exitting current account: " + currentAccount.accountName + "...");
                        exitInput = true;

                        Thread.sleep(2000);

                        break;
                
                    default:
                        System.out.println("Choose again!");

                        break;
                }

                if (exitInput) { break; }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();

            e.printStackTrace();
        }
    }

    public void beginTransfering() {
        AccountConsole.printTransferConsole();

        String choice;

        outer: while ((choice = input.nextLine()) != null) {
            switch (choice) {
                case "1":
                    

                    break outer;
                case "2":
                    try {
                        consoleActions.transferToThisClientAccount();

                    } catch (Exception e) {
                        Thread.currentThread().interrupt();

                        e.printStackTrace();
                    }
                    
                    break outer;
                case "3":
                    System.out.println("Exitting");
                    break outer;
                default:
                    System.out.println("Choose again!");

                    break;
            }

        }
    }
}