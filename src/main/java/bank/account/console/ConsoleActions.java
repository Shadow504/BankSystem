package bank.account.console;

import java.util.Scanner;
import bank.account.AccountDAO;
import bank.client.ClientClass;
import shared.Shared;

//* This class handles the console's actions when inputed */
public class ConsoleActions {

    Scanner input = Shared.input;

    private ClientClass client;
    private AccountDAO accountDAO;

    //* Each controller should hold a console action class  */
    public ConsoleActions(ClientClass client, AccountDAO accountDAO) {
        this.client = client;
        this.accountDAO = accountDAO;
    }

    //* Make a wrapper to handle the shared snippet */
    private void consoleWrapper(Runnable funcInBetween) throws InterruptedException {

        funcInBetween.run();

        Thread.sleep(2000);
        
        AccountConsole.printAccountConsole(client.getCurrentAccount());
    }

    public void beginDepositing() throws InterruptedException {

        consoleWrapper((Runnable) () -> {
            System.out.println("Deposit amount: ");
    
            int DepositAmount = Integer.parseInt(input.nextLine());
    
            if (client.getCurrentAccount().deposit(DepositAmount)) {
                System.out.println("Deposited Successfully, account balance is now: " + client.getCurrentAccount().balance);
            }
        });

    }

    public void beginWithdrawing() throws InterruptedException {
        consoleWrapper((Runnable) () -> {
            System.out.println("Withdraw amount: ");
    
            int withdrawAmount = Integer.parseInt(input.nextLine());
    
            if (client.getCurrentAccount().withdraw(withdrawAmount)) {
                System.out.println("Withdrew Successfully, account balance is now: " + client.getCurrentAccount().balance); 
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
                    
            client.getCurrentAccount().transfer(targetId, transferAmount);

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
                int clientTargetId = client.getClientDAO().getClientIdWithName(input.nextLine());

                int accountTargetId = accountDAO.getAccountId(getTargetToTransfer(), clientTargetId);

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

                int targetId = accountDAO.getAccountId(targetName, client.id);

                checkAndTransfer(targetId, transferAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }
}