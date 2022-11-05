package bank.account.console;

import java.util.Scanner;
import bank.account.AccountQueries;
import bank.client.ClientClass;
import bank.client.ClientQueries;
import shared.Shared;

//* This class handles the console's actions when inputed */
public class ConsoleActions {

    Scanner input = Shared.input;

    ClientClass client;

    //* Each controller should hold a console action class  */
    public ConsoleActions(ClientClass client) {
        this.client = client;
    }

    //* Make a wrapper to handle the shared snippet */
    private void consoleWrapper(Runnable funcInBetween) throws InterruptedException {

        funcInBetween.run();

        Thread.sleep(2000);
        
        AccountConsole.printAccountConsole(client.accountController.currentAccount);
    }

    public void beginDepositing() throws InterruptedException {

        consoleWrapper((Runnable) () -> {
            System.out.println("Deposit amount: ");
    
            int DepositAmount = Integer.parseInt(input.nextLine());
    
            if (client.accountController.currentAccount.deposit(DepositAmount)) {
                System.out.println("Deposited Successfully, account balance is now: " + client.accountController.currentAccount.balance);
            }
        });

    }

    public void beginWithdrawing() throws InterruptedException {
        consoleWrapper((Runnable) () -> {
            System.out.println("Withdraw amount: ");
    
            int withdrawAmount = Integer.parseInt(input.nextLine());
    
            if (client.accountController.currentAccount.withdraw(withdrawAmount)) {
                System.out.println("Withdrew Successfully, account balance is now: " + client.accountController.currentAccount.balance); 
            }
        });
    }

    private String getTargetToTransfer() {
        System.out.println("Transfer to (Account's name): ");
        
        return input.nextLine();
    }

    private int getAmountToTransfer() {
        System.out.println("Transfer amount: ");

        return Integer.parseInt(input.nextLine());
    }

    private void checkAndTransfer(int targetId, int transferAmount) {
        if (targetId > 0) {
                    
            client.accountController.currentAccount.transfer(targetId, transferAmount);

            System.out.println("Transfered successfully!");
            return;
        }

        System.out.println("Target account does not exist");
    }

    //* Transfer to another client's account */
    public void transferToNewClientAccount() throws InterruptedException {
        consoleWrapper((Runnable) () -> {
            try {
                System.out.println("Enter a client's name: ");
                int clientTargetId = ClientQueries.getClientIdWithName(input.nextLine());

                int accountTargetId = AccountQueries.getAccountId(getTargetToTransfer(), clientTargetId);

                int transferAmount = getAmountToTransfer();

                checkAndTransfer(accountTargetId, transferAmount);

            } catch (Exception e) {
                e.printStackTrace();
            }
    
        });

    }

    //* Transfer to an account that is in the current client */
    public void transferToThisClientAccount() throws InterruptedException {
        consoleWrapper((Runnable) () -> {
            try {

                String targetName = getTargetToTransfer();

                int transferAmount = getAmountToTransfer();

                int targetId = AccountQueries.getAccountId(targetName, client.accountController.currentClient.id);

                checkAndTransfer(targetId, transferAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }
}